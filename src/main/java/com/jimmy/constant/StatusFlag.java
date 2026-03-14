package com.jimmy.constant;

import lombok.Getter;

/**
 * 状态标志枚举
 */
@Getter
public enum StatusFlag {

    // 启用
    INVALID(0),
    // 禁用
    VALID(1);

    private final Integer flag;

    StatusFlag(Integer flag) {
        this.flag = flag;
    }
}
