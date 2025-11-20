package dev.techno.health_tracker.controller;

import dev.techno.health_tracker.dto.ChatRequestMessageDto;
import dev.techno.health_tracker.dto.ChatResponseMessageDto;
import dev.techno.health_tracker.service.LLMChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final LLMChatService chatService;

    @MessageMapping("/send-message")
    @SendTo("/topic/answers")
    public ResponseEntity<ChatResponseMessageDto> sendMessage(ChatRequestMessageDto chatRequestMessageDto) {
        String response = chatService.generateResponse(chatRequestMessageDto.message());
        return ResponseEntity.ok(new ChatResponseMessageDto(response));
    }

}
