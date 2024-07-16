package com.example.matchup.matchupbackend.mongodb;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "chatMessage")
public class ChatMessageV2 {
    @MongoId
    private String chatId;
    private Long roomId;
    private String message;
    private Integer isRead;
    private LocalDateTime sendTime;
    private String messageType;
    private Long senderID;

    @Builder
    public ChatMessageV2(Long roomId, String message, Integer isRead, LocalDateTime sendTime, String messageType, Long senderID) {
        this.chatId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.message = message;
        this.isRead = isRead;
        this.sendTime = sendTime;
        this.messageType = messageType;
        this.senderID = senderID;
    }

    public static ChatMessageV2 fromDTO(ChatMessageRequest chatMessageRequest) {
        return ChatMessageV2.builder()
                .roomId(chatMessageRequest.getRoomId())
                .message(chatMessageRequest.getMessage())
                .messageType(chatMessageRequest.getType().getValue())
                .senderID(chatMessageRequest.getSender().getUserId())
                .build();
    }

    public static ChatMessageV2 initChatRoom(User roomMaker, Long roomId) {
        return ChatMessageV2.builder()
                .roomId(roomId)
                .message(roomMaker.getNickname() + "님이 채팅방을 개설하였습니다.")
                .messageType("ENTER")
                .senderID(roomMaker.getId())
                .build();
    }

    public void readMessage() {
        this.isRead = 1;
    }
}
