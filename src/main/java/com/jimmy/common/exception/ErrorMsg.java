package com.jimmy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMsg {
    NOT_LOGIN("未登录"),
    MEDIA_TYPE_ERROR("媒体类型错误"),
    DATE_FORMAT_ERROR("日期"),
    TOKEN_CHECK_ERROR("Token 无效或已过期"),
    INVALID_TOKEN("缺少必要参数【token】"),
    EXIST_IN_BLACKLIST("token已在黑名单中，你已被强制下线");
    private final String msg;
}
