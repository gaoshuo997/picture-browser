package com.jimmy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMsg {
    NOT_LOGIN("未登录"),
    MEDIA_TYPE_ERROR("媒体类型错误"),
    DATE_FORMAT_ERROR("日期");
    private final String msg;
}
