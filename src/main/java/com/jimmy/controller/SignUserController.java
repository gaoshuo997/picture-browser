package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.result.Result;
import com.jimmy.req.AssignUserRoleDTO;
import com.jimmy.req.SignUserSave;
import com.jimmy.resp.RoleResp;
import com.jimmy.resp.SignUserResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.SignUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sign-user")
public class SignUserController {

    @Resource
    private SignUserService signUserService;

    // 分页查询
    @GetMapping("/page")
    public Result<PaginatedApiResult<SignUserResp>> list(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "loginName", required = false) String loginName,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
            @RequestParam(value = "createTimeStart", required = false) LocalDateTime startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
            @RequestParam(value = "createTimeEnd", required = false) LocalDateTime endDate) {

        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<SignUserResp> result = signUserService.list(page, size, loginName, startDate, endDate);
        return Result.success(result);
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(
            @PathVariable @NotNull(message = "用户ID不能为空") Long id,
            @Valid @RequestBody SignUserSave save) {
        signUserService.update(id, save);
        return Result.success();
    }

    /**
     * 删除用户
     * @param id 删除用户的id
     * @return void
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(
            @PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        signUserService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询用户拥有的角色
     * @param id 用户ID
     * @return 角色ID集合
     */
    @GetMapping("/{id}/roles")
    public Result<List<Long >> getRolesByUser(
            @PathVariable @NotNull(message = "用户ID不能为空") Long id){
        return Result.success(signUserService.getRolesByUserId(id));
    }

    /**
     * 查询登录用户拥有的角色列表
     * @return 角色列表
     */
    @GetMapping("/ownerRoles")
    public Result<List<RoleResp>> getRolesByOwner(){
        Long id = SecurityUtils.getCurrentUserId();
        return Result.success(signUserService.getRolesByOwner(id));
    }

    /**
     * 为用户分配角色
     * @param id 分配用户的ID
     * @return void
     */
    @PostMapping("/{id}/assign-roles")
    public Result<Void> assignUserRoles(
            @PathVariable @NotNull(message = "用户ID不能为空") Long id,
            @RequestBody AssignUserRoleDTO dto){
        signUserService.assignRoles(id, dto.getRoleIds());
        return Result.success();
    }
}