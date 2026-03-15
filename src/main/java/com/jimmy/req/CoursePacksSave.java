package com.jimmy.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CoursePacksSave {

    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 30, message = "标题长度必须在{min}~{max}个字符之间")
    private String title;

    @Size(min = 2, max = 60, message = "描述长度必须在{min}~{max}个字符之间")
    private String description;

    @NotBlank(message = "封面不能为空")
    private String cover;

    @NotBlank(message = "封面图片文件名不能为空")
    private String coverFileName;

    private Boolean free;
}
