package com.jimmy.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKeyName {
    MEDIA_PRE_SiGN_URL("media_presign_url");

    private final String name;
}
