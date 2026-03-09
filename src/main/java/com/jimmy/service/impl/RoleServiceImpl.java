package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.entity.Menu;
import com.jimmy.entity.Role;
import com.jimmy.entity.RoleMenu;
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

import java.util.ArrayList;
import java.util.Date;
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
                Sort.by(Sort.Direction.DESC, "id"));
        RoleReq req = new RoleReq();
        Specification<Role> roleSpecification = buildSpecification(req);
        Page<Role> rolePage = roleRepository.findAll(roleSpecification, pageable);
        List<RoleResp> respList = rolePage.getContent().stream().map(role -> {
            RoleResp resp = new RoleResp();
            BeanUtils.copyProperties(role,resp);
//            resp.setRoleCode(role.getRoleCode() != null ? role.getRoleCode().name() : null);
            resp.setCreateAt(DateUtils.format(role.getCreateAt(),DateUtils.DATETIME_FORMAT));
            return resp;
        }).toList();

        PaginatedApiResult<RoleResp> result = new PaginatedApiResult<>();
        result.setPage(pageable.getPageNumber());
        result.setPageSize(pageable.getPageSize());
        result.setList(respList);
        result.setTotal(rolePage.getTotalElements());
        result.setCount(respList.size());
        result.setTotalPages(rolePage.getTotalPages());
        return result;
    }

    @Override
    public List<String> getMenuIdsByRoleId(Long id) {
        List<RoleMenu> byRoleId = roleMenuRepository.findByRoleId(id);
        if (!byRoleId.isEmpty()){
            return byRoleId.stream().map(r -> r.getMenu().getId().toString()).toList();
        }
        return List.of();
    }

    @Override
    public void assignMenus(RoleMenuSave save) {
        Role role = checkRoleIsExist(save.getRoleId());
        List<Menu> menuListByIdIn = menuRepository.findByIdIn(new ArrayList<>(save.getMenuIds()));
        if (menuListByIdIn.size() != save.getMenuIds().size()){
            throw new RuntimeException("存在非法的菜单ID");
        }

        // 先删除
        roleMenuRepository.deleteByRoleId(role.getId());
        Date now = new Date();
        List<RoleMenu> roleMenuList = new ArrayList<>(save.getMenuIds().size());
        for (Menu menu : menuListByIdIn){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRole(role);
            roleMenu.setMenu(menu);
            roleMenu.setCreateAt(now);
            roleMenuList.add(roleMenu);
        }
        roleMenuRepository.saveAll(roleMenuList);
    }

    @Override
    public Role create(RoleSave save) {
        if (roleRepository.existsRolesByRoleCode((save.getRoleCode()))){
            throw new RuntimeException("该角色编码已存在");
        }
        if (roleRepository.existsRolesByRoleName(save.getRoleName())){
            throw new RuntimeException("该角色名称已存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(save, role);
        role.setCreateAt(new Date());
        role.setUpdateAt(new Date());
        return roleRepository.save(role);
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
        role.setRoleName(save.getRoleName());
        role.setDescription(save.getDescription());
        role.setStatus(save.getStatus());
        role.setUpdateAt(new Date());

        role = roleRepository.save(role);
        RoleResp resp = new RoleResp();
        BeanUtils.copyProperties(role, resp);
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
            predicates.add(cb.equal(root.get("deleteFlag"),0));
            if (req.getId() != null){
                predicates.add(cb.equal(root.get("id"), req.getId()));
            }
//            if (UserUtils.getUserId() != null){
//                predicates.add(cb.equal(root.get("userId"), UserUtils.getUserId()));
//            }
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