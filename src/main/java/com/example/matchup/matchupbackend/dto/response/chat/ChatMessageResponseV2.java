package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.dto.MessageSender;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.mongodb.ChatMessageV2;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponseV2 {
    private MessageSender sender;
    private String type;
    private String message;
    private Integer isRead;
    private LocalDateTime sendTime;

    public static ChatMessageResponseV2 from(ChatMessageV2 chatMessage, User sendUser, UserPosition senderPosition) {
        MessageSender sender = MessageSender.builder()
                .userId(chatMessage.getSenderID())
                .nickname(sendUser.getNickname())
                .pictureUrl(sendUser.getPictureUrl())
                .level(senderPosition.getTypeLevel())
                .build();
        return ChatMessageResponseV2.builder()
                .sender(sender)
                .type(chatMessage.getMessageType())
                .message(chatMessage.getMessage())
                .isRead(chatMessage.getIsRead())
                .sendTime(chatMessage.getSendTime())
                .build();
    }
}
