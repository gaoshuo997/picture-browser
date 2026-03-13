package com.jimmy.constant;

import lombok.Getter;

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
    STATUS("status");

    private final String name;
    PredicateFieldName(String name) {
        this.name = name;
    }
}
