package com.jimmy.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LearningProgressReq {

    @NotNull(message = "学习句子id不能为空")
    private Long statementId;

    @NotNull(message = "学习时长不能为空")
    private Integer duration;

    @NotNull(message = "学习个数不能为空")
    private Integer count;
}
