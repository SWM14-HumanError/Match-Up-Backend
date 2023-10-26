package com.example.matchup.matchupbackend.dto.request.chat;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageRequest {

    @Data
    @NoArgsConstructor
    public class Sender {
        private Long userId;
        private String nickname;
        private String pictureUrl;
    }

    @Getter
    public enum MessageType {
        ENTER("ENTER", "채팅방에 들어 온 경우"),
        CHAT("CHAT", "메세지"),
        LEAVE("LEAVE", "채팅방에서 나간 경우");
        private String value;
        private String description;
        MessageType(String value, String description) {
            this.value = value;
            this.description = description;
        }
    }

    private MessageType type;
    private Long roomId;
    private Sender sender;
    private String message;
}
