package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.resp.RoleResp;

public interface RoleService {
    PaginatedApiResult<RoleResp> list(Integer page, Integer pageSize);
}