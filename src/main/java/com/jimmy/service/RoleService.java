package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.entity.Role;
import com.jimmy.req.RoleMenuSave;
import com.jimmy.req.RoleSave;
import com.jimmy.resp.RoleResp;

import java.util.List;

public interface RoleService {
    PaginatedApiResult<RoleResp> list(Integer page, Integer pageSize);

    List<String> getMenuIdsByRoleId(Long id);

    void assignMenus(RoleMenuSave save);

    Role create(RoleSave save);

    void deleteById(Long id);

    RoleResp update(Long id, RoleSave save);
}