package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.dto.response.chat.SliceChatMessageResponseV2;
import com.example.matchup.matchupbackend.dto.response.chat.SliceChatRoomResponse;
import com.example.matchup.matchupbackend.entity.ChatRoom;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserChatRoom;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidChatException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.mongodb.ChatMessageV2;
import com.example.matchup.matchupbackend.mongodb.ChatMessageV2Repository;
import com.example.matchup.matchupbackend.repository.ChatRoomRepository;
import com.example.matchup.matchupbackend.repository.UserChatRoomRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.UserPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceV2 {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final ChatMessageV2Repository chatMessageV2Repository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final UserPositionRepository userPositionRepository;

    /**
     * 채팅 메세지를 MongoDB에 저장
     */
    @Transactional
    public void saveChatMessage(ChatMessageRequest chatMessageRequest) {
        ChatMessageV2 chatMessage = ChatMessageV2.fromDTO(chatMessageRequest);
        chatMessageV2Repository.save(chatMessage);
    }

    /**
     * 1대1 채팅방 생성
     */
    @Transactional
    public Long create1To1Room(String authorizationHeader, Long receiverId) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "create1To1Room");
        if (userId == receiverId) throw new InvalidChatException("자기 자신과는 채팅방을 생성할 수 없습니다.", "userID: " + userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        User opponent = userRepository.findById(receiverId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        ChatRoom chatRoom = ChatRoom.make1to1ChatRoom(user.getNickname());
        ChatRoom save = chatRoomRepository.save(chatRoom);
        link1To1RoomToUser(chatRoom, user, opponent);
        makeInitialChat(user, save.getId());
        log.info("userID: " + userId + " 님이 " + "userID: " + receiverId + "와 1대1 채팅방을 생성하였습니다.");
        return chatRoom.getId();
    }

    /**
     * 1대1 채팅방을 유저에게 연결
     */
    @Transactional
    public void link1To1RoomToUser(ChatRoom chatRoom, User user, User receiver) {
        UserChatRoom userChatRoom = UserChatRoom.createUserChatRoom(user, receiver, chatRoom);
        UserChatRoom receiverChatRoom = UserChatRoom.createUserChatRoom(receiver, user, chatRoom);
        userChatRoomRepository.save(userChatRoom);
        userChatRoomRepository.save(receiverChatRoom);
    }

    /**
     * 채팅방 생성시 초기 메세지 생성
     */
    @Transactional
    public void makeInitialChat(User roomMaker, Long roomId) {
        chatMessageV2Repository.save(ChatMessageV2.initChatRoom(roomMaker, roomId));
    }

    /**
     * 이미 유저와 만들어진 채팅방이 있는지 확인
     */
    public Long getChatRoomIdIfExist(String myToken, Long opponentId) {
        Long myId = tokenProvider.getUserId(myToken, "isExistChatRoom");
        Optional<UserChatRoom> myUserChatRoom = userChatRoomRepository.findUserChatRoomJoinChatRoomBy(myId, opponentId);
        if (myUserChatRoom.isEmpty()) {
            return 0L;
        }
        return myUserChatRoom.get().getChatRoom().getId();
    }

    /**
     * 채팅방 최근 메세지 보기
     */
    public SliceChatMessageResponseV2 showMessages(String token, Long roomId, Pageable pageable) {
        Long myId = tokenProvider.getUserId(token, "showMessages");
        Slice<ChatMessageV2> sortChatMessageByCreatedAt = chatMessageV2Repository.findByRoomIdOrderBySendTime(roomId, pageable);
        UserChatRoom userChatRoom = userChatRoomRepository.findJoinOpponentByChatRoomId(roomId, myId)
                .orElseThrow(() -> new InvalidChatException("채팅방의 상대 유저가 존재하지 않습니다."));
        List<UserPosition> userPosition = userPositionRepository.findAllByUser(userChatRoom.getOpponent());
        return SliceChatMessageResponseV2.from(myId, sortChatMessageByCreatedAt, userChatRoom.getOpponent(), getMaxLevel(userPosition));
    }

    private UserPosition getMaxLevel(List<UserPosition> userPosition) {
        if(userPosition.isEmpty()) {
            return UserPosition.builder().typeLevel(0).build();
        }
        return userPosition.stream()
                .max(Comparator.comparing(UserPosition::getTypeLevel))
                .orElseThrow(() -> new InvalidChatException("getMaxLevel"));
    }

    /**
     * 상대방이 보낸 메세지만 읽음 처리
     */
    @Transactional
    public void makeMessageRead(Long myId, List<ChatMessageV2> chatMessageList) {
        chatMessageList.forEach(chatMessage -> {
            if (!chatMessage.getSenderID().equals(myId)) {
                chatMessage.readMessage();
            }
        });
        chatMessageV2Repository.saveAll(chatMessageList);
    }

    /**
     * 채팅방 목록 보기
     */
    public SliceChatRoomResponse getSliceChatRoomResponse(String authorizationHeader, Pageable pageable) {
        Long myId = tokenProvider.getUserId(authorizationHeader, "getSliceChatRoomResponse");
        Slice<UserChatRoom> userChatRoomList = userChatRoomRepository.findJoinChatRoomAndUserByUserId(myId, pageable);
        Slice<UserChatRoom> updateRoomList = updateRoomInfo(myId, userChatRoomList);
        List<User> opponentList = updateRoomList.getContent().stream().map(UserChatRoom::getUser).collect(Collectors.toList());
        List<UserPosition> userPositionList = userPositionRepository.findAllJoinUserByUserList(opponentList);
        return SliceChatRoomResponse.from(myId, updateRoomList, mapUserPositionToUser(userPositionList));
    }

    private Map<User, Optional<UserPosition>> mapUserPositionToUser(List<UserPosition> userPositionList) {
        Map<User, Optional<UserPosition>> userPositionMap = new HashMap<>();
        userPositionList.forEach(userPosition -> {
            userPositionMap.put(userPosition.getUser(), Optional.ofNullable(userPosition));
        });
        return userPositionMap;
    }

    /**
     * 채팅방 정보 업데이트
     * 채팅방을 열면 채팅방의 정보를 업데이트 한다.
     */
    @Transactional
    public Slice<UserChatRoom> updateRoomInfo(Long myId, Slice<UserChatRoom> userChatRoomList) {
        userChatRoomList.forEach(userChatRoom -> {
            ChatRoom chatRoom = userChatRoom.getChatRoom();
            List<ChatMessageV2> chatMessageList = chatMessageV2Repository.findByRoomId(userChatRoom.getChatRoom().getId());
            userChatRoom.updateRoomInfoV2(myId, chatMessageList);
            chatRoom.updateRoomInfoV2(chatMessageList);
        });
        return userChatRoomList;
    }

}
