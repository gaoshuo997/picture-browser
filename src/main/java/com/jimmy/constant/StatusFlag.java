package com.jimmy.constant;

import lombok.Getter;

/**
 * 状态标志枚举
 */
@Getter
public enum StatusFlag {

    // 禁用
    INVALID(0),
    // 启用
    VALID(1);

    private final Integer flag;

    StatusFlag(Integer flag) {
        this.flag = flag;
    }
}
