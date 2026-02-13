package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.resp.RoleResp;
import com.jimmy.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

//    @GetMapping("/${id}/menus")
//    public ApplicationResponseEntity<List<String>> test() {
//        return "test";
//    }
}