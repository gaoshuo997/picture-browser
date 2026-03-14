package com.jimmy.constant;

import lombok.Getter;

/**
 * 删除状态枚举值
 */
@Getter
public enum DeleteFlag {
    // 删除
    DELETE(1),
    // 未删除
    NORMAL(0);

    private final Integer flag;

    DeleteFlag(Integer flag) {
        this.flag = flag;
    }
}
