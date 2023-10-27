package com.example.matchup.matchupbackend.dto.response.chat;

import lombok.Data;

import java.util.List;

@Data
public class SliceChatMessageResponse {
    private List<ChatMessageResponse> chatMessageResponseList;
    private int size;
    private Boolean hasNextSlice;
}