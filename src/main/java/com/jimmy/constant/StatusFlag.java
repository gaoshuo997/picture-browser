package com.jimmy.constant;

import lombok.Getter;

@Getter
public enum StatusFlag {

    INVALID(0),
    VALID(1);

    private final Integer flag;

    StatusFlag(Integer flag) {
        this.flag = flag;
    }
}
