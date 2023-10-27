package com.example.matchup.matchupbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageSender {
    private Long userId;
    private String nickname;
    private String pictureUrl;
}
