package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.dto.response.chat.SliceChatMessageResponse;
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
        Long userId = tokenProvider.getUserId(authorizationHeader, "createRoom");
        if (userId == receiverId) throw new InvalidChatException("userID: " + userId, "자기 자신과는 채팅방을 생성할 수 없습니다.");
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

    /**
     * 채팅방 최근 메세지 보기
     */
    public SliceChatMessageResponse showMessages(String token, Long roomId, Pageable pageable) {
        Long myId = tokenProvider.getUserId(token, "showMessages");
        Slice<ChatMessage> chatMessage = pagingChatMessageRepository.findByRoomId(roomId, pageable);
        Slice<ChatMessage> sortChatMessage = sortChatMessageByCreatedAt(chatMessage);
        return SliceChatMessageResponse.from(myId, sortChatMessage);
    }

    private Slice<ChatMessage> sortChatMessageByCreatedAt(Slice<ChatMessage> chatMessage) {
        List<ChatMessage> content = chatMessage.stream()
                .sorted(Comparator.comparing(ChatMessage::getSendTime))
                .collect(Collectors.toList());
        makeMessageRead(content);
        return new SliceImpl<>(content, chatMessage.getPageable(), chatMessage.hasNext());
    }

    private void makeMessageRead(List<ChatMessage> chatMessageList) {
        chatMessageList.forEach(chatMessage -> chatMessage.readMessage());
        chatMessageRepository.saveAll(chatMessageList);
    }
}
