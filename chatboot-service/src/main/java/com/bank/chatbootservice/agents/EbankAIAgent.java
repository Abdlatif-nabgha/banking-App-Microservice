package com.bank.chatbootservice.agents;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

@Service
public class EbankAIAgent {

    private final ChatClient chatClient;

    public EbankAIAgent(ChatClient.Builder chatClient, ChatMemory chatMemory, ToolCallbackProvider tools) {
        this.chatClient = chatClient
                .defaultSystem("You are an AI Banking Assistant. Answer user questions using the provided tools when needed.")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        stripReasoningAdvisor()
                ).defaultTools(tools)
                .build();
    }

    public String chat(String query) {
        return chatClient.prompt(query)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "default"))
                .call()
                .content();
    }

    private CallAdvisor stripReasoningAdvisor() {
        return new CallAdvisor() {
            @Override
            public @NonNull String getName() {
                return "StripReasoningMetadataAdvisor";
            }

            @Override
            public int getOrder() {
                return Ordered.LOWEST_PRECEDENCE - 1;
            }

            @Override
            public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
                request.prompt().getInstructions().forEach(m -> {
                    m.getMetadata().remove("reasoning_content");
                    m.getMetadata().remove("reasoning");
                });
                ChatClientResponse response = chain.nextCall(request);
                if (response.chatResponse() != null) {
                    response.chatResponse().getResults().forEach(r -> {
                        r.getOutput().getMetadata().remove("reasoning_content");
                        r.getOutput().getMetadata().remove("reasoning");
                    });
                }
                return response;
            }
        };
    }
}
