package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class MenuResp {
    private Long id;
    private String name;
    private String icon;
    private String path;
    private List<MenuResp> children;
    private Integer order;
    private Long parentId;
}