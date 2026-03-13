package com.jimmy.service;

import com.jimmy.req.MenuSave;
import com.jimmy.resp.MenuResp;

import java.util.List;

public interface MenuService {
    List<MenuResp> getMenuListByUserId(Long userId);

    void saveMenu(MenuSave menuSave);

    void updateMenu(Long id, MenuSave menuSave);

    void deleteMenuById(Long id);
}