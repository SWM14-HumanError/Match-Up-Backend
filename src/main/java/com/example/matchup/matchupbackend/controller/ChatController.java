package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.response.chat.SliceChatMessageResponse;
import com.example.matchup.matchupbackend.dto.response.chat.SliceChatRoomResponse;
import com.example.matchup.matchupbackend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@RestController
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room/{receiverId}")
    @Operation(description = "1대1 채팅방 생성")
    public Long createRoom(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable Long receiverId) {
        return chatService.create1To1Room(authorizationHeader, receiverId);
    }

    @GetMapping("/room/{roomID}")
    @Operation(description = "채팅 메세지 보기")
    public SliceChatMessageResponse showMessage(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable Long roomID, Pageable pageable) {
        return chatService.showMessages(authorizationHeader, roomID, pageable);
    }

    @GetMapping("/room")
    @Operation(description = "채팅방 목록 보기")
    public SliceChatRoomResponse getRoomList(@RequestHeader(value = "Authorization") String authorizationHeader, Pageable pageable) {
        return chatService.getSliceChatRoomResponse(authorizationHeader, pageable);
    }

    @GetMapping("/user/{opponentId}")
    @Operation(description = "유저와 채팅방이 이미 있는지 확인하는 API")
    public Long checkChatRoom(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable Long opponentId) {
        return chatService.getChatRoomIdIfExist(authorizationHeader, opponentId);
    }
}
