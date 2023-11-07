package com.example.matchup.matchupbackend.dto.response.chat;

import com.example.matchup.matchupbackend.dto.MessageSender;
import com.example.matchup.matchupbackend.entity.UserChatRoom;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomResponse {
    private Long chatRoomId;
    private MessageSender sender;
    private Long peopleCount;
    private Long unreadCount;
    private String lastChat;
    private LocalDateTime lastChatTime;

    public static ChatRoomResponse from(UserChatRoom userChatRoom) {
        MessageSender sender = MessageSender.builder()
                .userId(userChatRoom.getOpponent().getId())
                .nickname(userChatRoom.getRoomName())
                .pictureUrl(userChatRoom.getOpponent().getPictureUrl())
                .level(userChatRoom.getOpponent().getPositionLevel())
                .build();

        return ChatRoomResponse.builder()
                .chatRoomId(userChatRoom.getChatRoom().getId())
                .sender(sender)
                .peopleCount(userChatRoom.getChatRoom().getPeopleCount())
                .unreadCount(userChatRoom.getUnreadChatCount())
                .lastChat(userChatRoom.getChatRoom().getLastChat())
                .lastChatTime(userChatRoom.getChatRoom().getLastChatTime())
                .build();
    }
}
