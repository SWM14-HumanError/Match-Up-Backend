package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
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

    /**
     * 1대1 채팅 보내기
     */
    @MessageMapping("/chat/{roomId}") //"pub/chat/{roomId}"
    @SendToUser("/sub-queue/chat/{roomId}")
    public String sendMessage(@DestinationVariable Long roomId, ChatMessageRequest message) {
        log.info(message.toString()); //todo 서비스단에서 채팅 저장하고 채팅 저장된 객체를 리턴
        return message.getMessage();
    }

    /**
     * 단체 채팅 보내기
     */
    @MessageMapping("/chats/{roomId}") //"pub/chats/{roomId}"
    @SendTo("/sub-topic/chat/{roomId}")
    public String sendMessages(@DestinationVariable Long roomId, ChatMessageRequest message) {
        log.info(message.toString());
        return message.getMessage();
    }




    @MessageMapping("/chat/test/{roomId}") //"queue/chat/pub/{roomId}", "topic/chat/pub/{roomId}"
    @SendToUser("/queue/chat/test/{roomId}")
    public String sendMessage(@DestinationVariable Long roomId, String message) {
        log.info(message);
        return message;
    }
}
