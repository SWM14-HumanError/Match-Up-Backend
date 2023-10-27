package com.example.matchup.matchupbackend.global.annotation;

import lombok.Getter;

@Getter
public enum  MessageType {
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
