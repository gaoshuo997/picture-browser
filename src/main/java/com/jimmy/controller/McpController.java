package com.jimmy.controller;

import com.jimmy.service.McpService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class McpController {

    @Resource
    private McpService mcpService;

    @PostMapping("/generate")
    public Flux<String> chat(
            @RequestParam String sessionId,
            @RequestParam String userInput) {
        return mcpService.chatWithMemory(sessionId, userInput);
    }

    // 清空对话记录
    @DeleteMapping("/clear")
    public String clear(@RequestParam String sessionId){
        mcpService.clearMemory(sessionId);
        return "SUCCESS";
    }
}
