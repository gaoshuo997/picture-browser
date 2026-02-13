package com.jimmy.service.impl;

import com.jimmy.entity.Menu;
import com.jimmy.entity.RoleMenu;
import com.jimmy.entity.UserRole;
import com.jimmy.entity.enums.RoleCode;
import com.jimmy.repository.MenuRepository;
import com.jimmy.repository.RoleMenuRepository;
import com.jimmy.repository.UserRoleRepository;
import com.jimmy.resp.MenuResp;
import com.jimmy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    @Override
    public List<MenuResp> getMenuListByUserId(Long userId) {
        // 1. 查询用户的角色
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        Optional<UserRole> any = userRoles.stream()
                .filter(userRole -> userRole.getRole().getRoleCode().equals(RoleCode.ADMIN)).findAny();

        // 如果是超级管理员则获取所有菜单
        if (any.isPresent()){
            return buildMenuTree(menuRepository.findAll());
        }
        // 2. 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(ur -> ur.getRole().getId())
                .toList();

        // 3. 查询角色对应的菜单关联
        List<RoleMenu> roleMenus = roleMenuRepository.findByRoleIdIn(roleIds);
        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 获取菜单ID列表
        List<Long> menuIds = roleMenus.stream()
                .map(rm -> rm.getMenu().getId())
                .distinct()
                .toList();

        // 5. 查询菜单详情
        List<Menu> menus = menuRepository.findByIdIn(menuIds);
        menus = menus.stream()
                .filter(m -> m.getDeleteFlag() == null || m.getDeleteFlag() == 0)
                .sorted((a, b) -> {
                    Integer sortA = a.getSortOrder() != null ? a.getSortOrder() : 0;
                    Integer sortB = b.getSortOrder() != null ? b.getSortOrder() : 0;
                    return sortA.compareTo(sortB);
                })
                .toList();

        // 6. 构建菜单树
        return buildMenuTree(menus);
    }

    private List<MenuResp> buildMenuTree(List<Menu> menus) {
        List<MenuResp> result = new ArrayList<>();
        Map<Long, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, m -> m));
        List<Menu> rootMenus = menus.stream().filter(m -> m.getParentMenu() == null).toList();

        for (Menu rootMenu : rootMenus) {
            MenuResp menuResp = convertToMenuResp(rootMenu);
            buildChildren(menuResp, rootMenu, menuMap);
            result.add(menuResp);
        }

        return result;
    }

    private void buildChildren(MenuResp parentResp, Menu parentMenu, Map<Long, Menu> menuMap) {
        List<MenuResp> children = new ArrayList<>();
        for (Menu menu : menuMap.values()) {
            if (menu.getParentMenu() != null && menu.getParentMenu().getId().equals(parentMenu.getId())) {
                MenuResp childResp = convertToMenuResp(menu);
                buildChildren(childResp, menu, menuMap);
                children.add(childResp);
            }
        }
        children.sort((a, b) -> Long.compare(Long.parseLong(a.getId()), Long.parseLong(b.getId())));
        if (!children.isEmpty()) {
            parentResp.setChildren(children);
        }
    }

    private MenuResp convertToMenuResp(Menu menu) {
        MenuResp resp = new MenuResp();
        resp.setId(String.valueOf(menu.getId()));
        resp.setName(menu.getName());
        resp.setIcon(menu.getIcon());
        resp.setPath(menu.getPath());
        resp.setChildren(null);
        return resp;
    }
}
