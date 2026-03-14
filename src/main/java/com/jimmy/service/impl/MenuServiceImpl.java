package com.jimmy.service.impl;

import com.jimmy.constant.DeleteFlag;
import com.jimmy.entity.Menu;
import com.jimmy.entity.RoleMenu;
import com.jimmy.entity.UserRole;
import com.jimmy.entity.enums.RoleCode;
import com.jimmy.req.MenuSave;
import com.jimmy.repository.MenuRepository;
import com.jimmy.repository.RoleMenuRepository;
import com.jimmy.repository.UserRoleRepository;
import com.jimmy.resp.MenuResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.MenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
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

        // 如果是超级管理员则获取所有菜单
        if (SecurityUtils.hasRole(RoleCode.ADMIN.toString())){
            List<MenuResp> menuRespList = buildMenuTree(menuRepository.findAllByDeleteFlag(0));
            menuRespList.sort(Comparator.comparingInt(MenuResp::getOrder));
            return menuRespList;
        }

        // 2. 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        // 3. 查询角色对应的菜单关联
        List<RoleMenu> roleMenus = roleMenuRepository.findByRoleIdIn(roleIds);
        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 获取菜单ID列表
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .distinct()
                .toList();

        // 5. 查询菜单详情
        List<Menu> menus = menuRepository.findByIdInAndDeleteFlag(menuIds,DeleteFlag.NORMAL.getFlag());

        // 6. 构建菜单树
        List<MenuResp> respList = buildMenuTree(menus);
        respList.sort(Comparator.comparingInt(MenuResp::getOrder));
        return respList;
    }

    private List<MenuResp> buildMenuTree(List<Menu> menus) {
        List<MenuResp> result = new ArrayList<>();
        Map<Long, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, m -> m));
        List<Menu> rootMenus = menus.stream().filter(m -> m.getParentId() == null).toList();

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
            if (menu.getParentId() != null && menu.getParentId().equals(parentMenu.getId())) {
                MenuResp childResp = convertToMenuResp(menu);
                buildChildren(childResp, menu, menuMap);
                children.add(childResp);
                childResp.setParentId(parentMenu.getId());
            }
        }
        children.sort(Comparator.comparingInt(MenuResp::getOrder));
        if (!children.isEmpty()) {
            parentResp.setChildren(children);
        }
    }

    private MenuResp convertToMenuResp(Menu menu) {
        MenuResp resp = new MenuResp();
        BeanUtils.copyProperties(menu,resp);
        resp.setChildren(null);
        return resp;
    }

    @Override
    public void saveMenu(MenuSave menuSave) {
        Menu menu = new Menu();
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(menuSave, menu);
        menu.setCreateAt(now);
        menu.setUpdateAt(now);
        menu.setDeleteFlag(DeleteFlag.NORMAL.getFlag());

        if (menuSave.getParentId() != null) {
            menuRepository.findById(menuSave.getParentId())
                    .orElseThrow(() -> new RuntimeException("父菜单不存在"));
            menu.setParentId(menuSave.getParentId());
        }

        menuRepository.save(menu);
    }

    @Override
    public void updateMenu(Long id, MenuSave menuSave) {
        Menu menu = new Menu();
        LocalDateTime now = LocalDateTime.now();
        menu.setId(id);
        BeanUtils.copyProperties(menuSave, menu);
        menu.setUpdateAt(now);
        menu.setDeleteFlag(DeleteFlag.NORMAL.getFlag());

        if (menuSave.getParentId() != null) {
            menuRepository.findById(menuSave.getParentId())
                    .orElseThrow(() -> new RuntimeException("父菜单不存在"));
            menu.setParentId(menuSave.getParentId());
        }
        menuRepository.save(menu);
    }

    @Override
    public void deleteMenuById(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("菜单不存在"));
        menu.setDeleteFlag(DeleteFlag.DELETE.getFlag());
        menuRepository.save(menu);
    }
}
