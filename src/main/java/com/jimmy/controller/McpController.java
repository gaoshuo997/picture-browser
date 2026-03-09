package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.req.AIChatReq;
import com.jimmy.resp.AIChatResp;
import com.jimmy.service.McpService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class McpController {

    @Resource
    private McpService mcpService;

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
