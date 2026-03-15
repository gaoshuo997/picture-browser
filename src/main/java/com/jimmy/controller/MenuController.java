package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.req.MenuSave;
import com.jimmy.resp.MenuResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 菜单管理接口（只能管理员可以访问）
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单列表（根据用户权限返回对应菜单）
     * @return 菜单列表
     */
    @GetMapping("/list")
    public Result<List<MenuResp>> getMenuList() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<MenuResp> menuList = menuService.getMenuListByUserId(userId);
        return Result.success(menuList);
    }

    /**
     * 新建菜单
     * @param menuSave 菜单保存参数
     * @return 空
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/create")
    public Result<Void> saveMenu(@Valid @RequestBody MenuSave menuSave) {
        menuService.saveMenu(menuSave);
        return Result.success();
    }

    /**
     * 更新菜单
     * @param id 菜单ID
     * @param menuSave 菜单更新参数
     * @return 空
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update/{id}")
    public Result<Void> updateMenu(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id,
            @Valid @RequestBody MenuSave menuSave){
        menuService.updateMenu(id, menuSave);
        return Result.success();
    }

    /**
     * 删除菜单
     * @param id 菜单ID
     * @return void
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMenu(@PathVariable @NotNull(message = "菜单ID不能为空") Long id){
        menuService.deleteMenuById(id);
        return Result.success();
    }
}