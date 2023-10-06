package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final TokenProvider tokenProvider;
    private final LikeService likeService;
    @PostMapping("/user/{user_id}/like")
    public String saveLikeToUser(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long user_id) {
        Long likeGiverID = tokenProvider.getUserId(authorizationHeader, "giveLikeToUser");
        likeService.saveLikeToUser(likeGiverID, user_id);
        log.info("userID: " + user_id + " 에게 좋아요를 눌렀습니다.");
        return "userID: " + user_id + " 에게 좋아요를 눌렀습니다.";
    }
}
