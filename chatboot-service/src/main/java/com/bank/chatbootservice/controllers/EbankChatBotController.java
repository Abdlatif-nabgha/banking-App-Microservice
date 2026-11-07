package com.bank.chatbootservice.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EbankChatBotController {

    private final ChatClient chatClient;

    public EbankChatBotController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(String query) {
        return chatClient.prompt(query).call().content();
    }

}
