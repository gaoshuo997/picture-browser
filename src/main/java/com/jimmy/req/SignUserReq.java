package com.jimmy.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUserReq {

    @NotBlank(message = "用户名不能为空")
    @Min(value = 4, message = "用户名长度不能少于4个字符")
    @Max(value = 8, message = "用户名长度不能超过8个字符")
    private String loginName;

    @NotBlank
    @Min(value = 6, message = "密码长度不能少于6个字符")
    @Max(value = 16, message = "密码长度不能超过16个字符")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;
}
