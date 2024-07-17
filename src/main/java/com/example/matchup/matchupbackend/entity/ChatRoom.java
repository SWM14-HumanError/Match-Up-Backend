package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dynamodb.ChatMessage;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidChatException;
import com.example.matchup.matchupbackend.mongodb.ChatMessageV2;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;
    private Long peopleCount;
    private String lastChat;
    private LocalDateTime lastChatTime;

    @OneToMany(mappedBy = "chatRoom")
    private List<UserChatRoom> userChatRoom;
    //todo 나중에 채팅 분류도 고려
    @Builder
    public ChatRoom(String name, Long peopleCount, Long unreadChatCount, String lastChat, LocalDateTime lastChatTime, List<UserChatRoom> userChatRoom, Long opponentId, String opponentIMG) {
        this.peopleCount = peopleCount;
        this.lastChat = lastChat;
        this.lastChatTime = lastChatTime;
        this.userChatRoom = userChatRoom;
    }

    //==비즈니스 로직==//
    public static ChatRoom make1to1ChatRoom(String roomMaker) {
        return ChatRoom.builder()
                .peopleCount(2L)
                .lastChat(roomMaker + "님이 대화방을 개설하셨습니다.")
                .lastChatTime(LocalDateTime.now())
                .build();
    }

    public void updateRoomInfo(List<ChatMessage> chatMessageList) {
        updateLastChatInfo(findLastChatMessage(chatMessageList));
    }

    private void updateLastChatInfo(ChatMessage chatMessage) {
        this.lastChat = chatMessage.getMessage();
        this.lastChatTime = chatMessage.getSendTime();
    }

    private ChatMessage findLastChatMessage(List<ChatMessage> chatMessageList) {
        return chatMessageList.stream().max(Comparator.comparing(ChatMessage::getSendTime)).orElseThrow(() -> new InvalidChatException("채팅방에 메세지가 없습니다."));
    }

    /**
     * MongoDB로 마이그레이션 - V2
     */

    public void updateRoomInfoV2(List<ChatMessageV2> chatMessageList) {
        updateLastChatInfoV2(findLastChatMessageV2(chatMessageList));
    }

    private void updateLastChatInfoV2(ChatMessageV2 chatMessage) {
        this.lastChat = chatMessage.getMessage();
        this.lastChatTime = chatMessage.getSendTime();
    }

    private ChatMessageV2 findLastChatMessageV2(List<ChatMessageV2> chatMessageList) {
        return chatMessageList.stream().max(Comparator.comparing(ChatMessageV2::getSendTime)).orElseThrow(() -> new InvalidChatException("채팅방에 메세지가 없습니다."));
    }
}
