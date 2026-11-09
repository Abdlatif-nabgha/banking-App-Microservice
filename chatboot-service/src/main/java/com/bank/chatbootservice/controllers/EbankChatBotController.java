package com.bank.chatbootservice.controllers;

import com.bank.chatbootservice.agents.EbankAIAgent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AI Chatbot", description = "Operations related to the AI Banking Chatbot")
public class EbankChatBotController {

    private final EbankAIAgent ebankAIAgent;

    public EbankChatBotController(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Ask AI Chatbot", description = "Send a question or prompt to the AI Banking Assistant")
    public String chat(
            @RequestParam(name = "query", defaultValue = "Hello") String query) {
        return ebankAIAgent.chat(query);
    }

}
