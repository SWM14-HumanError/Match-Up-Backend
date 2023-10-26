package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@RestController
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room/{receiverId}")
    @Operation(description = "1대1 채팅방 생성")
    public String createRoom(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable Long receiverId) {
        chatService.create1To1Room(authorizationHeader, receiverId);
        return "1대1 채팅방 생성";
    }

    @GetMapping ("/room")  //todo 더 공부해 이거에 대해서
    @Operation(description = "채팅 메세지 보기")
    public void showMessage(@RequestParam String name) {

    }

    //todo 채팅방 생성, 채팅 메세지 보기, 채팅방 나가기
}
