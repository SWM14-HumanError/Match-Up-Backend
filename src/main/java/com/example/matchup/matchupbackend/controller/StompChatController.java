package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidChatException;
import com.example.matchup.matchupbackend.service.ChatServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompChatController {
    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatServiceV2 chatService;

    /**
     * 1대1 채팅 보내기
     */
    @MessageMapping("/chat/{roomId}") //"pub/chat/{roomId}"
    @SendToUser("/sub-queue/chat/{roomId}")
    public ChatMessageRequest sendMessage(@DestinationVariable Long roomId, ChatMessageRequest message) {
        if (!roomId.equals(message.getRoomId()))
            throw new InvalidChatException("잘못된 roomID와 송신하고 있습니다. "
                    + "pathRoomID: " + roomId + "--" + "DTORoomID: " + message.getRoomId());
        chatService.saveChatMessage(message);
        messagingTemplate.convertAndSend("/sub-queue/chat/" + roomId, message);
        return message;
    }

    /**
     * 단체 채팅 보내기
     */
    @MessageMapping("/chats/{roomId}") //"pub/chats/{roomId}"
    @SendTo("/sub-topic/chat/{roomId}")
    public ChatMessageRequest sendMessages(@DestinationVariable Long roomId, ChatMessageRequest message) {
        chatService.saveChatMessage(message);
        return message;
    }

}
