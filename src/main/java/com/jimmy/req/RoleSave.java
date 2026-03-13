package com.jimmy.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoleSave {

    @NotBlank(message = "角色名不能为空" )
    @Size(max = 20, message = "角色名长度不能超过20个字符")
    private String roleName;

    @NotBlank(message = "角色编码不能为空" )
    @Size(max = 20, message = "角色编码长度不能超过20个字符")
    private String roleCode;

    @Size(max = 128, message = "描述长度不能超过200个字符")
    private String description;

    @NotNull(message = "状态不能为空" )
    private Integer status;

    private List<Long> menuIds;
}
