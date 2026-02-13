package com.jimmy.service;

import com.jimmy.resp.MenuResp;

import java.util.List;

public interface MenuService {
    List<MenuResp> getMenuListByUserId(Long userId);
}