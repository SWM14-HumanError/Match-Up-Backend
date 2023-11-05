package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.dto.MessageSender;
import com.example.matchup.matchupbackend.dynamodb.ChatMessage;
import com.example.matchup.matchupbackend.entity.User;
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

    public static ChatMessageResponse from(ChatMessage chatMessage, User sendUser){
        MessageSender sender = MessageSender.builder()
                .userId(chatMessage.getSenderID())
                .nickname(sendUser.getNickname())
                .pictureUrl(sendUser.getPictureUrl())
                .level(sendUser.getPositionLevel())
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