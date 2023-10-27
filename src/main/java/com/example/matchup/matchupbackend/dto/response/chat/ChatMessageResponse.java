package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.dto.MessageSender;
import com.example.matchup.matchupbackend.dynamodb.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private MessageSender sender;
    private String type;
    private String message;
    private Integer isRead;
    private LocalDateTime sendTime;

    public static ChatMessageResponse from(ChatMessage chatMessage){
        MessageSender sender = MessageSender.builder()
                .userId(chatMessage.getSenderID())
                .nickname(chatMessage.getSenderName())
                .pictureUrl(chatMessage.getSenderFace())
                .build();
        return ChatMessageResponse.builder()
                .sender(sender)
                .type(chatMessage.getMessageType())
                .message(chatMessage.getMessage())
                .isRead(chatMessage.getIsRead())
                .sendTime(chatMessage.getSendTime())
                .build();
    }
}