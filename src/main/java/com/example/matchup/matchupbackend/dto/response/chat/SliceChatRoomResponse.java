package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.entity.UserChatRoom;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SliceChatRoomResponse {
    private List<ChatRoomResponse> chatRoomResponseList;
    private int size;
    private Boolean hasNextSlice;
    private Long myId;

    public static SliceChatRoomResponse from(Long myId, Slice<UserChatRoom> userChatRoomSlice) {
        List<ChatRoomResponse> chatRoomResponseList = new ArrayList<>();
        userChatRoomSlice.getContent().forEach(userChatRoom -> {
            chatRoomResponseList.add(ChatRoomResponse.from(userChatRoom));
        });
        return SliceChatRoomResponse.builder()
                .chatRoomResponseList(chatRoomResponseList)
                .size(userChatRoomSlice.getSize())
                .hasNextSlice(userChatRoomSlice.hasNext())
                .myId(myId)
                .build();
    }
}
