package com.jimmy.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MenuSave {

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 64, message = "菜单名称长度不能超过64个字符")
    private String name;

    @Size(max = 64, message = "图标长度不能超过64个字符")
    private String icon;

    @Size(max = 128, message = "路径长度不能超过128个字符")
    private String path;

    private Long parentId;

    @NotNull(message = "排序不能为空")
    private Integer order;
}