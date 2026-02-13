package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class MenuResp {
    private String id;
    private String name;
    private String icon;
    private String path;
    private List<MenuResp> children;
}