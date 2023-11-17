package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserChatRoom;
import com.example.matchup.matchupbackend.entity.UserPosition;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Builder
public class SliceChatRoomResponse {
    private List<ChatRoomResponse> chatRoomResponseList;
    private int size;
    private Boolean hasNextSlice;
    private Long myId;

    public static SliceChatRoomResponse from(Long myId, Slice<UserChatRoom> userChatRoomSlice,Map<User, Optional<UserPosition>> userPositionMap) {
        List<ChatRoomResponse> chatRoomResponseList = new ArrayList<>();
        userChatRoomSlice.getContent().forEach(userChatRoom -> {
            chatRoomResponseList.add(ChatRoomResponse.from(userChatRoom, userPositionMap.get(userChatRoom.getOpponent())));
        });
        return SliceChatRoomResponse.builder()
                .chatRoomResponseList(chatRoomResponseList)
                .size(userChatRoomSlice.getSize())
                .hasNextSlice(userChatRoomSlice.hasNext())
                .myId(myId)
                .build();
    }
}
