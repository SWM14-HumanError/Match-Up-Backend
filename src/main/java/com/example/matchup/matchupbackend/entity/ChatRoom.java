package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;
    @Column(name = "chatroom_name")
    private String name;
    private Long peopleCount;
    private Long unreadChatCount; // 내가 안읽은 메세지 갯수
    private String lastChat;
    private LocalDateTime lastChatTime;
    @OneToMany(mappedBy = "chatRoom")
    private List<UserChatRoom> userChatRoom;

    @Builder
    public ChatRoom(String name, Long peopleCount, Long unreadChatCount, String lastChat, LocalDateTime lastChatTime, List<UserChatRoom> userChatRoom) {
        this.name = name;
        this.peopleCount = peopleCount;
        this.unreadChatCount = unreadChatCount;
        this.lastChat = lastChat;
        this.lastChatTime = lastChatTime;
        this.userChatRoom = userChatRoom;
    }

    //==비즈니스 로직==//
    public static ChatRoom make1to1ChatRoom(String roomName, String roomMaker) {
        return ChatRoom.builder()
                .name(roomName)
                .peopleCount(2L)
                .unreadChatCount(1L)
                .lastChat(roomMaker + "님이 대화방을 개설하셨습니다.")
                .lastChatTime(LocalDateTime.now())
                .build();
    }
}
