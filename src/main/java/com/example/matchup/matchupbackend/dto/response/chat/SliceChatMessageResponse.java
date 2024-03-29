package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.dynamodb.ChatMessage;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SliceChatMessageResponse {
    private List<ChatMessageResponse> chatMessageResponseList;
    private int size;
    private Boolean hasNextSlice;
    private Long myId;

    public static SliceChatMessageResponse from(Long myId, Slice<ChatMessage> slice, User opponent, UserPosition userPosition) {
        List<ChatMessageResponse> content = new ArrayList<>();

        slice.getContent().forEach(chatMessage -> {
            content.add(ChatMessageResponse.from(chatMessage, opponent, userPosition));
        });
        return SliceChatMessageResponse.builder()
                .chatMessageResponseList(content)
                .size(slice.getSize())
                .hasNextSlice(slice.hasNext())
                .myId(myId)
                .build();
    }
}