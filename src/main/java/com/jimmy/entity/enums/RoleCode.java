package com.jimmy.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleCode {

    ADMIN("超级管理员"),

    USER("普通用户");

    private final String name;
}
