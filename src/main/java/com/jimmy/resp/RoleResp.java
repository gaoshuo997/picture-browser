package com.jimmy.resp;

import lombok.Data;

import java.util.List;

@Data
public class RoleResp {
    private Long id;

    private String roleName;

    private String roleCode;

    private String description;

    private String createAt;

    private Integer status;

    private Long menuCount;

    private List<Long> menuIds;
}