package com.example.matchup.matchupbackend.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageSender {
    private Long userId;
    private String nickname;
    private String pictureUrl;
    @Builder
    public MessageSender(Long userId, String nickname, String pictureUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.pictureUrl = pictureUrl;
    }
}
