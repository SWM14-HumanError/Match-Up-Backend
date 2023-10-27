package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.dto.MessageSender;
import com.example.matchup.matchupbackend.global.annotation.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private MessageSender sender;
    private MessageType type;
    private String message;
    private Integer isRead;
    private LocalDateTime sendTime;
}