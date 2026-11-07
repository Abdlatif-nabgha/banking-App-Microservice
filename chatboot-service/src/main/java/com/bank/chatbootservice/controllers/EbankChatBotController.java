package com.bank.chatbootservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AI Chatbot", description = "Operations related to the AI Banking Chatbot")
public class EbankChatBotController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public EbankChatBotController(ChatClient.Builder chatClient, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClient
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                ).build();
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Ask AI Chatbot", description = "Send a question or prompt to the AI Banking Assistant")
    public String chat(
            @RequestParam(name = "query", defaultValue = "Hello") String query
    ) {
        chatMemory.get("default").forEach(m -> m.getMetadata().remove("reasoning_content"));
        return chatClient.prompt(query)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "default"))
                .call()
                .content();
    }

}
