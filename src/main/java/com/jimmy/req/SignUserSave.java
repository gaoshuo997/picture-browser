package com.jimmy.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUserSave {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 8, message = "用户名长度必须在4-8个字符之间")
    private String loginName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 16, message = "密码长度必须在6-16个英文字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密码只能是英文字符和数字")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 32, message = "邮箱长度不能超过32个字符")
    private String email;

    @Size(max = 16, message = "手机号长度不能超过16个字符")
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}