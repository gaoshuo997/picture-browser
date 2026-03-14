package com.jimmy.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * 角色code 枚举值
 */
@Getter
@AllArgsConstructor
public enum RoleCode {

    ADMIN("超级管理员"),

    USER("普通用户");

    private final String name;
}
