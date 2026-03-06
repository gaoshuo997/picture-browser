package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.req.RoleMenuSave;
import com.jimmy.req.RoleSave;
import com.jimmy.resp.RoleResp;
import com.jimmy.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/page")
    public ApplicationResponseEntity<PaginatedApiResult<RoleResp>> list(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer size) {

        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<RoleResp> result = roleService.list(page, size);
        ApplicationResponseEntity<PaginatedApiResult<RoleResp>> response = new ApplicationResponseEntity<>();
        response.setData(result);
        return response;
    }

    @GetMapping("/menus/{id}")
    public ApplicationResponseEntity<List<String>> getRoleMenuIds(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id) {
        List<String> menuIdsByRoleId = roleService.getMenuIdsByRoleId(id);
        ApplicationResponseEntity<List<String>> result = new ApplicationResponseEntity<>();
        result.setData(menuIdsByRoleId);
        return result;
    }

    /**
     * 新增角色
     */
    @PostMapping("/create")
    public ApplicationResponseEntity<RoleResp> add(
            @Valid @RequestBody RoleSave save){
        RoleResp resp = new RoleResp();
        BeanUtils.copyProperties(roleService.create(save), resp);
        ApplicationResponseEntity<RoleResp> result = new ApplicationResponseEntity<>();
        result.setData(resp);
        return result;
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public ApplicationResponseEntity<RoleResp> update(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id,
            @Valid @RequestBody RoleSave save){
        RoleResp resp = roleService.update(id, save);
        ApplicationResponseEntity<RoleResp> result = new ApplicationResponseEntity<>();
        result.setData(resp);
        return result;
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ApplicationResponseEntity<Map<String, String>> delete(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id){
        roleService.deleteById(id);

        ApplicationResponseEntity<Map<String, String>> result = new ApplicationResponseEntity<>();
        Map<String, String> resultMap = new HashMap<>(1);
        resultMap.put("message","删除成功");
        result.setData(resultMap);
        return result;
    }

    /**
     * 为角色分配菜单
     */
    @PostMapping("/assign-menus")
    public ApplicationResponseEntity<Map<String, String>> assignMenus(@Valid @RequestBody RoleMenuSave save){
        roleService.assignMenus(save);
        ApplicationResponseEntity<Map<String, String>> result = new ApplicationResponseEntity<>();
        Map<String, String> resultMap = new HashMap<>(1);
        resultMap.put("message","分配成功");
        result.setData(resultMap);
        return result;
    }
}