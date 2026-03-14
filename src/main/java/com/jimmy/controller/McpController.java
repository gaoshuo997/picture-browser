package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.req.AIChatReq;
import com.jimmy.resp.AIChatResp;
import com.jimmy.service.McpService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户聊天服务
 */
@RestController
@RequestMapping("/chat")
public class McpController {

    @Resource
    private McpService mcpService;

    /**
     * ai回答用户提问
     * @param req 问题
     * @return 回答信息
     */
    @PostMapping("/generate")
    public Result<AIChatResp> chat(
            @Valid @RequestBody AIChatReq req) {
        return Result.success(mcpService.chatWithMemory(req.getSessionId(), req.getUserInput()));
    }

    // 清空对话记录
    @DeleteMapping("/clear")
    public Result<?> clear(@RequestParam String sessionId){
        mcpService.clearMemory(sessionId);
        return Result.success();
    }
}
