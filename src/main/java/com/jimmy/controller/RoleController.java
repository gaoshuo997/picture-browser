package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.result.Result;
import com.jimmy.req.RoleMenuSave;
import com.jimmy.req.RoleSave;
import com.jimmy.resp.RoleResp;
import com.jimmy.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色控制器（只允许管理员进入）
 */
@RestController
@RequestMapping("/role")
@PreAuthorize("hasAnyRole('ADMIN')")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页获取角色列表
     */
    @GetMapping("/page")
    public Result<PaginatedApiResult<RoleResp>> list(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer size) {

        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<RoleResp> result = roleService.list(page, size);
        return Result.success(result);
    }

    /**
     * 获取角色对应的菜单ID集合
     */
    @GetMapping("/menus/{id}")
    public Result<List<String>> getRoleMenuIds(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id) {
        List<String> menuIdsByRoleId = roleService.getMenuIdsByRoleId(id);
        return Result.success(menuIdsByRoleId);
    }

    /**
     * 新增角色
     */
    @PostMapping("/create")
    public Result<RoleResp> add(
            @Valid @RequestBody RoleSave save){
        return Result.success(roleService.create(save));
    }

    /**
     * 更新角色
     */
    @PutMapping("/update/{id}")
    public Result<RoleResp> update(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id,
            @Valid @RequestBody RoleSave save){
        RoleResp resp = roleService.update(id, save);
        return Result.success(resp);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id){
        roleService.deleteById(id);
        return Result.success();
    }

    /**
     * 为角色分配菜单
     */
    @PostMapping("/assign-menus")
    public Result<?> assignMenus(@Valid @RequestBody RoleMenuSave save){
        roleService.assignMenus(save);
        return Result.success();
    }

    /**
     * 获取角色详细信息
     */
    @GetMapping("/{id}")
    public Result<RoleResp> get(@PathVariable @NotNull(message = "角色ID不能为空") Long id){
        return Result.success(roleService.detail(id));
    }

    /**
     * 启用/禁用角色
     * @param id 角色ID
     * @return void
     */
    @PutMapping("/{id}/setStatus")
    public Result<Void> setStatus(@PathVariable @NotNull(message = "角色ID不能为空") Long id){
        roleService.setStatus(id);
        return Result.success();
    }
}