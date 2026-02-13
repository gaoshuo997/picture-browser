package com.jimmy.controller;

import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.resp.MenuResp;
import com.jimmy.service.MenuService;
import com.jimmy.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ApplicationResponseEntity<List<MenuResp>> getMenuList() {
        Long userId = UserUtils.getUserId();
        List<MenuResp> menuList = menuService.getMenuListByUserId(userId);
        ApplicationResponseEntity<List<MenuResp>> result = new ApplicationResponseEntity<>();
        result.setData(menuList);
        return result;
    }
}