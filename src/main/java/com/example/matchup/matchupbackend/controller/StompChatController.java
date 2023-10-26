package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final ChatService chatService;

    /**
     * 1대1 채팅 보내기
     */
    @MessageMapping("/chat/{roomId}") //"pub/chat/{roomId}"
    @SendToUser("/sub-queue/chat/{roomId}")
    public String sendMessage(@DestinationVariable Long roomId, ChatMessageRequest message) {
        chatService.saveChatMessage(message);
        return message.getMessage();
    }

    /**
     * 단체 채팅 보내기
     */
    @MessageMapping("/chats/{roomId}") //"pub/chats/{roomId}"
    @SendTo("/sub-topic/chat/{roomId}")
    public String sendMessages(@DestinationVariable Long roomId, ChatMessageRequest message) {
        chatService.saveChatMessage(message);
        return message.getMessage();
    }

}
