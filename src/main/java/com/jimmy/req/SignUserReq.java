package com.jimmy.req;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUserReq {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 8, message = "用户名长度必须在4-8个字符之间")
    private String loginName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 16, message = "密码长度必须在6-16个英文字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密码只能是英文字符和数字")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;
}
