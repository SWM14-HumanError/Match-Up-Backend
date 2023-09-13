package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.profile.UserProfileEditRequest;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileDetailResponse;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile/{user_id}")
    public ResponseEntity<UserProfileDetailResponse> showProfile(@PathVariable("user_id") Long userId) {
        UserProfileDetailResponse response = userProfileService.showUserProfile(userId);
        return (response != null)
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/profile/unique")
    public ResponseEntity<Void> checkDuplicateNickname(@RequestParam("nickname") String nickname) {
        if (nickname.length() > 20) {
            return ResponseEntity.badRequest().build();
        }
        userProfileService.checkDuplicateNickname(nickname);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile/{user_id}")
    public ResponseEntity<String> editProfile(@RequestHeader(TokenProvider.HEADER_AUTHORIZATION) String authorizationHeader,
                                            @PathVariable("user_id") Long userId,
                                            @Valid @RequestBody UserProfileEditRequest request) {

        String newToken = userProfileService.putUserProfile(authorizationHeader, userId, request);
        log.info("유저 id: {}의 프로필이 수정되었습니다.", userId);
        return (newToken != null) ? ResponseEntity.ok(newToken) : ResponseEntity.ok().build();
    }
}
