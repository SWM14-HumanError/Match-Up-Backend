package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.dto.response.chat.SliceChatMessageResponse;
import com.example.matchup.matchupbackend.dto.response.chat.SliceChatRoomResponse;
import com.example.matchup.matchupbackend.dynamodb.ChatMessage;
import com.example.matchup.matchupbackend.dynamodb.ChatMessageRepository;
import com.example.matchup.matchupbackend.dynamodb.PagingChatMessageRepository;
import com.example.matchup.matchupbackend.entity.ChatRoom;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserChatRoom;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidChatException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.ChatRoomRepository;
import com.example.matchup.matchupbackend.repository.UserChatRoomRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private final PagingChatMessageRepository pagingChatMessageRepository;

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
        Long userId = tokenProvider.getUserId(authorizationHeader, "create1To1Room");
        if (userId == receiverId) throw new InvalidChatException("자기 자신과는 채팅방을 생성할 수 없습니다.","userID: " + userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        User opponent = userRepository.findById(receiverId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        ChatRoom chatRoom = ChatRoom.make1to1ChatRoom(user.getNickname());
        ChatRoom save = chatRoomRepository.save(chatRoom);
        link1To1RoomToUser(chatRoom, user, opponent);
        makeInitialChat(user, save.getId());
        log.info("userID: " + userId + " 님이 " + "userID: " + receiverId + "와 1대1 채팅방을 생성하였습니다.");
        return chatRoom.getId();
    }

    @Transactional
    public void link1To1RoomToUser(ChatRoom chatRoom, User user, User receiver) {
        UserChatRoom userChatRoom = UserChatRoom.createUserChatRoom(user, receiver, chatRoom);
        UserChatRoom receiverChatRoom = UserChatRoom.createUserChatRoom(receiver, user, chatRoom);
        userChatRoomRepository.save(userChatRoom);
        userChatRoomRepository.save(receiverChatRoom);
    }

    /**
     * 채팅방 최근 메세지 보기
     */
    public SliceChatMessageResponse showMessages(String token, Long roomId, Pageable pageable) {
        Long myId = tokenProvider.getUserId(token, "showMessages");
        Slice<ChatMessage> chatMessage = pagingChatMessageRepository.findByRoomId(roomId, pageable);
        Slice<ChatMessage> sortChatMessage = sortChatMessageByCreatedAt(myId,chatMessage);
        return SliceChatMessageResponse.from(myId, sortChatMessage);
    }

    @Transactional
    public void makeInitialChat(User roomMaker, Long roomId) {
        chatMessageRepository.save(ChatMessage.initChatRoom(roomMaker, roomId));
    }

    /**
     * 채팅 메세지를 보낸 시간 순으로 정렬
     */
    private Slice<ChatMessage> sortChatMessageByCreatedAt(Long myId, Slice<ChatMessage> chatMessage) {
        List<ChatMessage> content = chatMessage.stream()
                .sorted(Comparator.comparing(ChatMessage::getSendTime))
                .collect(Collectors.toList());
        makeMessageRead(myId, content);
        return new SliceImpl<>(content, chatMessage.getPageable(), chatMessage.hasNext());
    }

    /**
     * 상대방에 보낸 메세지만 읽음 처리
     */
    @Transactional
    public void makeMessageRead(Long myId, List<ChatMessage> chatMessageList) {
        chatMessageList.forEach(chatMessage -> {
            if (!chatMessage.getSenderID().equals(myId)) {
                chatMessage.readMessage();
            }
        });
        chatMessageRepository.saveAll(chatMessageList);
    }

    /**
     * 채팅방 목록 보기
     */
    public SliceChatRoomResponse getSliceChatRoomResponse(String authorizationHeader, Pageable pageable){
        Long myId = tokenProvider.getUserId(authorizationHeader, "getSliceChatRoomResponse");
        Slice<UserChatRoom> userChatRoomList = userChatRoomRepository.findJoinChatRoomAndUserByUserId(myId, pageable);
        Slice<UserChatRoom> updateRoomList = updateRoomInfo(myId, userChatRoomList);
        return SliceChatRoomResponse.from(myId, updateRoomList);
    }

    /**
     * 채팅방 정보 업데이트
     * 채팅방을 열면 채팅방의 정보를 업데이트 한다.
     */
    @Transactional
    public Slice<UserChatRoom> updateRoomInfo(Long myId, Slice<UserChatRoom> userChatRoomList){
        userChatRoomList.forEach(userChatRoom -> {
            ChatRoom chatRoom = userChatRoom.getChatRoom();
            List<ChatMessage> chatMessageList = chatMessageRepository.findByRoomId(userChatRoom.getChatRoom().getId());
            userChatRoom.updateRoomInfo(myId, chatMessageList);
            chatRoom.updateRoomInfo(chatMessageList);
        });
        return userChatRoomList;
    }
}
