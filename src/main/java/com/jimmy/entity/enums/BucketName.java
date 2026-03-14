package com.jimmy.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * minio bucket名称
 */
@Getter
@AllArgsConstructor
public enum BucketName {
    MO_JING("mojing"),
    PUBLIC("public");

    private final String msg;
}
