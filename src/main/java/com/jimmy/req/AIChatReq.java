package com.jimmy.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AIChatReq {

    @NotBlank(message = "会话id不能为空")
    @Size(min = 1, max = 36, message = "会话id长度必须在1-36个字符之间")
    private String sessionId;

    @NotBlank(message = "用户输入内容不能为空")
    @Size(min = 1, max = 255, message = "会话id长度必须在1-255个字符之间")
    private String userInput;
}
