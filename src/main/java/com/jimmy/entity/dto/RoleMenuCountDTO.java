package com.jimmy.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMenuCountDTO {

    private Long roleId;

    private String roleName;

    private Long menuCount;

    public RoleMenuCountDTO() {}

    public RoleMenuCountDTO(Long roleId, String roleName, Long menuCount) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.menuCount = menuCount;
    }
}
