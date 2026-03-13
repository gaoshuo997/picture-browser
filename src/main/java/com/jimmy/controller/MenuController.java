package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.req.MenuSave;
import com.jimmy.resp.MenuResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public Result<List<MenuResp>> getMenuList() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<MenuResp> menuList = menuService.getMenuListByUserId(userId);
        return Result.success(menuList);
    }

    @PostMapping("/create")
    public Result<Void> saveMenu(@Valid @RequestBody MenuSave menuSave) {
        menuService.saveMenu(menuSave);
        return Result.success();
    }

    @PutMapping("/update/{id}")
    public Result<Void> updateMenu(
            @PathVariable @NotNull(message = "角色ID不能为空") Long id,
            @Valid @RequestBody MenuSave menuSave){
        menuService.updateMenu(id, menuSave);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMenu(@PathVariable @NotNull(message = "菜单ID不能为空") Long id){
        menuService.deleteMenuById(id);
        return Result.success();
    }
}