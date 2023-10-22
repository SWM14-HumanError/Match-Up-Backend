package com.example.matchup.matchupbackend.dto.request.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageRequest {
    private class Sender {
        private Long userId;
        private String nickname;
        private String pictureUrl;
    }

    private enum MessageType {
        ENTER, CHAT, LEAVE
    }
    private MessageType type;
    private String roomId;
    private Sender sender;
    private String message;
    private Integer readCount;
    private LocalDateTime sendTime;
}
