package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.common.result.BusinessException;
import com.jimmy.constant.DeleteFlag;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.constant.StatusFlag;
import com.jimmy.entity.*;
import com.jimmy.entity.dto.RoleMenuCountDTO;
import com.jimmy.repository.*;
import com.jimmy.req.RoleMenuSave;
import com.jimmy.req.RoleReq;
import com.jimmy.req.RoleSave;
import com.jimmy.resp.RoleResp;
import com.jimmy.service.RoleService;
import com.jimmy.utils.DateUtils;
import com.jimmy.utils.RoleMessageFormatter;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleRepository roleRepository;
    @Resource
    private RoleMenuRepository roleMenuRepository;
    @Resource
    private MenuRepository menuRepository;
    @Resource
    private UserRoleRepository userRoleRepository;
    @Resource
    private SignUserRepository signUserRepository;

    @Override
    public PaginatedApiResult<RoleResp> list(Integer page, Integer pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Direction.ASC, PredicateFieldName.ID.getName()));
        RoleReq req = new RoleReq();
        Specification<Role> roleSpecification = buildSpecification(req);
        Page<Role> rolePage = roleRepository.findAll(roleSpecification, pageable);

        List<Long> roleIds = rolePage.getContent().stream().map(Role::getId).toList();
        List<RoleMenuCountDTO> roleMenuCountDTOS = roleRepository.countMenusPerRole(roleIds);

        List<RoleResp> respList = rolePage.getContent().stream().map(role -> {
            RoleResp resp = new RoleResp();
            BeanUtils.copyProperties(role,resp);
            resp.setCreateAt(DateUtils.format(role.getCreateAt(),DateUtils.DATE_FORMAT));
            resp.setMenuCount(roleMenuCountDTOS.stream()
                    .filter(r -> r.getRoleId().equals(resp.getId()))
                    .findFirst().orElse(new RoleMenuCountDTO()).getMenuCount());
            return resp;
        }).toList();

        return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                respList.size(),rolePage.getTotalElements(),
                respList,rolePage.getTotalPages());
    }

    @Override
    public List<String> getMenuIdsByRoleId(Long id) {
        List<RoleMenu> byRoleId = roleMenuRepository.findByRoleId(id);
        if (!byRoleId.isEmpty()){
            return byRoleId.stream().map(r -> r.getMenuId().toString()).toList();
        }
        return List.of();
    }

    @Override
    public void assignMenus(RoleMenuSave save) {
        Role role = checkRoleIsExist(save.getRoleId());
        List<Long> distinctMenuIds = save.getMenuIds().stream().distinct().toList();
        List<Menu> menuListByIdIn = menuRepository.findByIdInAndDeleteFlag(distinctMenuIds,DeleteFlag.NORMAL.getFlag());
        if (menuListByIdIn.size() != distinctMenuIds.size()){
            throw new RuntimeException("存在非法的菜单ID");
        }

        // 先删除
        roleMenuRepository.deleteByRoleId(role.getId());
        LocalDateTime now = LocalDateTime.now();
        List<RoleMenu> roleMenuList = new ArrayList<>(distinctMenuIds.size());
        for (Menu menu : menuListByIdIn){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(role.getId());
            roleMenu.setMenuId(menu.getId());
            roleMenu.setCreateAt(now);
            roleMenuList.add(roleMenu);
        }
        roleMenuRepository.saveAll(roleMenuList);
    }

    @Override
    public RoleResp create(RoleSave save) {
        if (roleRepository.existsRolesByRoleCode((save.getRoleCode()))){
            throw new RuntimeException("该角色编码已存在");
        }
        if (roleRepository.existsRolesByRoleName(save.getRoleName())){
            throw new RuntimeException("该角色名称已存在");
        }
        Role role = new Role();
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(save, role);
        role.setCreateAt(now);
        role.setUpdateAt(now);
        RoleResp resp = new RoleResp();
        BeanUtils.copyProperties(roleRepository.save(role), resp);
        return resp;
    }

    @Override
    public void deleteById(Long id) {
        Role role = checkRoleIsExist(id);

        // 如果该角色已经分配给激活用户了，提示
        List<UserRole> userRolesByRoleId = userRoleRepository.findByRoleId(role.getId());
        if (!CollectionUtils.isEmpty(userRolesByRoleId)){
            Set<Long> userIds = userRolesByRoleId.stream()
                    .map(UserRole::getUserId).collect(Collectors.toSet());
            List<SignUser> byIdInAndStatus = signUserRepository.findByIdInAndStatus(userIds,
                    StatusFlag.VALID.getFlag());

            String message = new RoleMessageFormatter()
                    .format(byIdInAndStatus.stream().map(SignUser::getLoginName).toList());
            throw new BusinessException(BadReqExceptionMsg.EXIST_ROLE_BY_USER.getCode(),message);
        }

        // 删除角色菜单
        roleMenuRepository.deleteByRoleId(id);

        // 删除角色
        role.setDeleteFlag(DeleteFlag.DELETE.getFlag());
        role.setStatus(StatusFlag.INVALID.getFlag());
        role.setUpdateAt(LocalDateTime.now());
        roleRepository.save(role);
    }

    @Override
    public RoleResp update(Long id, RoleSave save) {
        Role role = checkRoleIsExist(id);
        LocalDateTime now = LocalDateTime.now();

        role.setRoleName(save.getRoleName());
        role.setDescription(save.getDescription());
        role.setStatus(Integer.valueOf(save.getStatus()));
        role.setUpdateAt(now);

        role = roleRepository.save(role);
        RoleResp resp = new RoleResp();
        BeanUtils.copyProperties(role, resp);
        return resp;
    }

    @Override
    public RoleResp detail(Long id) {
        Role role = checkRoleIsExist(id);
        RoleResp resp = new RoleResp();
        BeanUtils.copyProperties(role, resp);

        List<RoleMenu> byRoleId = roleMenuRepository.findByRoleId(id);
        resp.setMenuIds(byRoleId.stream().map(RoleMenu::getMenuId).toList());
        return resp;
    }

    @Override
    public void setStatus(Long id) {
        Role role = checkRoleIsExist(id);
        if (Objects.equals(role.getStatus(), StatusFlag.VALID.getFlag())){
            role.setStatus(StatusFlag.INVALID.getFlag());
        }else {
            role.setStatus(StatusFlag.VALID.getFlag());
        }
        role.setUpdateAt(LocalDateTime.now());
        roleRepository.save(role);
    }

    /**
     * 构建查询条件
     * @param req 请求查询条件
     * @return 返回查询条件
     */
    private Specification<Role> buildSpecification(RoleReq req){
        return (root, query, cb) -> {
            // 存储查询条件的集合
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(PredicateFieldName.DELETE_FLAG.getName()), DeleteFlag.NORMAL.getFlag()));
            if (req.getId() != null){
                predicates.add(cb.equal(root.get(PredicateFieldName.ID.getName()), req.getId()));
            }
            predicates.add(cb.equal(root.get(PredicateFieldName.STATUS.getName()), StatusFlag.VALID.getFlag()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

    /**
     * 检查角色是否存在
     */
    private Role checkRoleIsExist(Long id){
        return roleRepository.findRoleByIdAndStatus(id,StatusFlag.VALID.getFlag()).orElseThrow(() ->
                new RuntimeException("角色不存在"));
    }
}