package com.jimmy.constant;

import lombok.Getter;

/**
 * 查询字段（entity）名枚举
 */
@Getter
public enum PredicateFieldName {
    COURSE_ID("courseId"),
    USER_ID("userId"),
    LOGIN_NAME("loginName"),
    UPDATED_AT("updatedAt"),
    CREATED_AT("createdAt"),
    ID("id"),

    ORDER("order"),
    DELETE_FLAG("deleteFlag"),
    MEDIA_TYPE("mediaType"),
    STATUS("status"),
    FILE_NAME("fileName");

    private final String name;
    PredicateFieldName(String name) {
        this.name = name;
    }
}
