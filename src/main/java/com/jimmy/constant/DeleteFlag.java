package com.jimmy.constant;

import lombok.Getter;

@Getter
public enum DeleteFlag {
    DELETE(1),
    NORMAL(0);

    private final Integer flag;

    DeleteFlag(Integer flag) {
        this.flag = flag;
    }
}
