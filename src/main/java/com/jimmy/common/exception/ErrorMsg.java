package com.jimmy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMsg {
    NOT_LOGIN("未登录");
    private final String msg;
}
