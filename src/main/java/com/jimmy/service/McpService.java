package com.jimmy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
public class McpService {

    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Autowired
    private ChatMemory chatMemory;

    @Value("classpath:prompts/translator.st")
    private Resource translatorResource;

    public Flux<String> chatWithMemory(String sessionId, String userInput) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(translatorResource);

        // 这里的value后面改成可配置的，目前写死
        Message systemMessage = systemPromptTemplate.createMessage(Map.of(
                "language","英语",
                "originWord", "english",
                "targetWord", "chinese",
                "originLanguage", "英语",
                "targetLanguage", "中文"));

        log.info("systemMessage====:{}",systemMessage.getText());
        Prompt prompt = new Prompt(systemMessage);

        return deepSeekChatClient.prompt(prompt)
                .user(userInput)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .stream()
                .content();
    }

    /**
     * 清空指定会话的聊天记忆
     * @param sessionId 会话ID
     */
    public void clearMemory(String sessionId) {
        chatMemory.clear(sessionId);
    }

}
