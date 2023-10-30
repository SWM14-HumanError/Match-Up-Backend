package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dynamodb.ChatMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_chatroom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    private String roomName;

    private Long unreadChatCount;

    private Long opponentId;

    private String opponentIMG;

    @Builder
    public UserChatRoom(User user, ChatRoom chatRoom, String roomName, Long unreadChatCount, Long opponentId, String opponentIMG) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.roomName = roomName;
        this.unreadChatCount = unreadChatCount;
        this.opponentId = opponentId;
        this.opponentIMG = opponentIMG;
    }

    public static UserChatRoom createUserChatRoom(User user, User opponent, ChatRoom chatRoom) {
        return UserChatRoom.builder()
                .user(user)
                .chatRoom(chatRoom)
                .roomName(opponent.getNickname())
                .unreadChatCount(0L)
                .opponentId(opponent.getId())
                .opponentIMG(opponent.getPictureUrl())
                .build();
    }

    public void updateRoomInfo(Long myId, List<ChatMessage> chatMessageList){
        this.unreadChatCount = chatMessageList.stream().filter(chatMessage -> chatMessage.getIsRead() == 0 && !chatMessage.getSenderID().equals(myId)).count();
    }
}
