package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.constant.DeleteFlag;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.constant.StatusFlag;
import com.jimmy.entity.Menu;
import com.jimmy.entity.Role;
import com.jimmy.entity.RoleMenu;
import com.jimmy.entity.dto.RoleMenuCountDTO;
import com.jimmy.repository.MenuRepository;
import com.jimmy.repository.RoleMenuRepository;
import com.jimmy.req.RoleMenuSave;
import com.jimmy.req.RoleReq;
import com.jimmy.req.RoleSave;
import com.jimmy.resp.RoleResp;
import com.jimmy.repository.RoleRepository;
import com.jimmy.service.RoleService;
import com.jimmy.utils.DateUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMenuRepository roleMenuRepository;
    @Autowired
    private MenuRepository menuRepository;

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
        List<Menu> menuListByIdIn = menuRepository.findByIdInAndDeleteFlag(distinctMenuIds,0);
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

        // 删除角色菜单
        roleMenuRepository.deleteByRoleId(id);
        // 删除角色
        roleRepository.delete(role);
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
        return roleRepository.findById(id).orElseThrow(() ->
                new RuntimeException("角色不存在"));
    }
}