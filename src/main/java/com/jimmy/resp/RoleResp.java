package com.jimmy.resp;

import lombok.Data;

@Data
public class RoleResp {
    private Long id;

    private String roleName;

    private String roleCode;

    private String description;

    private String createAt;

    private Integer status;
}