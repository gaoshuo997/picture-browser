package com.jimmy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadReqExceptionMsg {

    SIGN_NUM_OVER(40001,"注册人数过多"),

    SIGN_ALREADY_EXIST(40002,"注册用户已存在"),

    SiGN_EMAIL_EXIST(40003,"注册邮箱已存在");

    private final int code;
    private final String message;
}
