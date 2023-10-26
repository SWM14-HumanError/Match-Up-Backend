package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.dynamodb.ChatMessage;
import com.example.matchup.matchupbackend.dynamodb.ChatMessageRepository;
import com.example.matchup.matchupbackend.entity.ChatRoom;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserChatRoom;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.ChatRoomRepository;
import com.example.matchup.matchupbackend.repository.UserChatRoomRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    /**
     * 채팅 메세지를 DynamoDB에 저장
     */
    @Transactional
    public void saveChatMessage(ChatMessageRequest chatMessageRequest){
        ChatMessage chatMessage = ChatMessage.fromDTO(chatMessageRequest);
        chatMessageRepository.save(chatMessage);
    }

    /**
     * 1대1 채팅방 생성
     */
    @Transactional
    public Long create1To1Room(String authorizationHeader, Long receiverId) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "createRoom");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        ChatRoom chatRoom = ChatRoom.make1to1ChatRoom(receiver.getNickname(), user.getNickname());
        chatRoomRepository.save(chatRoom);
        link1To1RoomToUser(chatRoom, user, receiver);
        log.info("userID: " + userId + " 님이 " + "userID: " + receiverId + "와 1대1 채팅방을 생성하였습니다.");
        return chatRoom.getId(); //npe 각
    }

    @Transactional
    public void link1To1RoomToUser(ChatRoom chatRoom, User user, User receiver) {
        UserChatRoom userChatRoom = UserChatRoom.createUserChatRoom(user, chatRoom);
        UserChatRoom receiverChatRoom = UserChatRoom.createUserChatRoom(receiver, chatRoom);
        userChatRoomRepository.save(userChatRoom);
        userChatRoomRepository.save(receiverChatRoom);
    }
}
