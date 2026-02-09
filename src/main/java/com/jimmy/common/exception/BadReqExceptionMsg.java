package com.jimmy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadReqExceptionMsg {

    SIGN_NUM_OVER(40001,"注册人数过多"),

    SIGN_ALREADY_EXIST(40002,"注册用户已存在"),

    SiGN_EMAIL_EXIST(40003,"注册邮箱已存在"),

    PASSWORD_ERROR(40004,"密码错误"),

    SIGN_USER_NOT_EXIST(40005,"该用户不存在"),

    UPLOAD_IMG_IS_NULL(40006,"上传文件为空！"),

    BUCKET_NAME_IS_NULL(40007,"minio桶名称不能为空"),

    UPLOAD_IMG_TYPE_ERROR(40008,"上传的图片格式错误"),

    UPLOAD_VIDEO_TYPE_ERROR(40009,"上传的视频格式错误"),

    REMOVE_OBJECT_ERROR(40010,"移除文件对像失败"),

    MEDIA_IS_NOT_EXIST(40011,"这个媒体文件不存在");

    private final int code;
    private final String message;
}
