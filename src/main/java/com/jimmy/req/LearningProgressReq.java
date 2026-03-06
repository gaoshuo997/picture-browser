package com.jimmy.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LearningProgressReq {

    @NotBlank(message = "学习句子id不能为空")
    private String statementId;

    @NotNull(message = "学习时长不能为空")
    private Integer duration;
}
