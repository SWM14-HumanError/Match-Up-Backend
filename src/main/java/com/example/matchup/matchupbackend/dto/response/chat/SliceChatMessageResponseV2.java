package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.mongodb.ChatMessageV2;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SliceChatMessageResponseV2 {
    private List<ChatMessageResponseV2> chatMessageResponseList;
    private int size;
    private Boolean hasNextSlice;
    private Long myId;

    /**
     * ChatMessage Entity에서 DTO로 변경 하여 전송
     */
    public static SliceChatMessageResponseV2 from(Long myId, Slice<ChatMessageV2> slice, User opponent, UserPosition userPosition) {
        List<ChatMessageResponseV2> content = new ArrayList<>();
        slice.getContent().forEach(chatMessage -> {
            content.add(ChatMessageResponseV2.from(chatMessage, opponent, userPosition));
        });

        return SliceChatMessageResponseV2.builder()
                .chatMessageResponseList(content)
                .size(slice.getSize())
                .hasNextSlice(slice.hasNext())
                .myId(myId)
                .build();
    }
}
