package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.profile.UserProfileEditRequest;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileDetailResponse;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileFeedbackResponse;
import com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateUserNicknameException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
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
            throw new DuplicateUserNicknameException();
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

    @GetMapping("/profile/{user_id}/feedbacks/{grade}")
    @Operation(description = "유저 프로필 페이지 / 피드백 API")
    public ResponseEntity<UserProfileFeedbackResponse> getFeedbacks(@PathVariable("user_id") Long userId, @PathVariable(value = "grade", required = false) String grade) {
        UserProfileFeedbackResponse response = userProfileService.getUserProfileFeedbacks(userId, grade);
        return (response != null)
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/profile/{user_id}/feedbacks/hide")
    @Operation(description = "피드백 숨김/공개를 변경 하는 API")
    public String hideFeedbacks(@PathVariable("user_id") Long userId) {
        return userProfileService.hideFeedbacks(userId);
    }

}
