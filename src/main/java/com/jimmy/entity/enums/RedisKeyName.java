package com.jimmy.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * redis key名称
 */
@Getter
@AllArgsConstructor
public enum RedisKeyName {
    MEDIA_PRE_SiGN_URL("media_presign_url");

    private final String name;
}
