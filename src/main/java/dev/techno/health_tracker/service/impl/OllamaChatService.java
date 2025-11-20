package dev.techno.health_tracker.service.impl;

import dev.techno.health_tracker.service.LLMChatService;
import org.springframework.stereotype.Service;

@Service
public class OllamaChatService implements LLMChatService {

    @Override
    public String generateResponse(String userMessage) {
        return "Everything will be just aright pally!";
    }

}
