package com.jimmy.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    @Qualifier("deepSeekChatModel")
    public DeepSeekChatModel deepSeekChatModel(){
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .build();
        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(DeepSeekChatOptions.builder().model("deepseek-chat").build())
                .build();
    }

    @Bean
    @Qualifier("deepSeekReasonerModel")
    public DeepSeekChatModel deepSeekReasonerModel(){
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .build();
        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(DeepSeekChatOptions.builder().model("deepseek-reasoner").build())
                .build();
    }

    @Bean
    public ChatClient deepSeekReasonerClient(
            @Qualifier("deepSeekReasonerModel") DeepSeekChatModel deepSeekReasonerModel,
            ChatMemory chatMemory) {
        return ChatClient.builder(deepSeekReasonerModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean
    public ChatClient deepSeekChatClient(
            @Qualifier("deepSeekChatModel") DeepSeekChatModel deepSeekChatModel,
            ChatMemory chatMemory){
        return ChatClient.builder(deepSeekChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
