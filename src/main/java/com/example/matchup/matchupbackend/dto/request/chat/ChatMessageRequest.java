package com.example.matchup.matchupbackend.dto.request.chat;

import com.example.matchup.matchupbackend.dto.MessageSender;
import com.example.matchup.matchupbackend.global.annotation.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageRequest {

    private MessageType type;
    private Long roomId;
    private MessageSender sender;
    private String message;
}
