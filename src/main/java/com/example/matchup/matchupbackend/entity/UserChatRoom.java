package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static UserChatRoom createUserChatRoom(User user, ChatRoom chatRoom) {
        UserChatRoom userChatRoom = new UserChatRoom();
        userChatRoom.user = user;
        userChatRoom.chatRoom = chatRoom;
        return userChatRoom;
    }
}
