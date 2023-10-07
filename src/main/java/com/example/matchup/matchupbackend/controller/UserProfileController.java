package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.user.ProfileRequest;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileDetailResponse;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileFeedbackResponse;
import com.example.matchup.matchupbackend.dto.response.profile.UserSettingStateResponse;
import com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateUserNicknameException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final TokenProvider tokenProvider;

    @GetMapping("/profile/{user_id}")
    public ResponseEntity<UserProfileDetailResponse> showProfile(@PathVariable("user_id") Long userId) {
        UserProfileDetailResponse response = userProfileService.showUserProfile(userId);
        return (response != null)
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/profile/unique")
    public ResponseEntity<Void> checkDuplicateNickname(@RequestParam("nickname") String nickname,
                                                       @RequestHeader(value = TokenProvider.HEADER_AUTHORIZATION, required = false) String authorizationHeader) {
        if (nickname.length() > 20) {
            throw new DuplicateUserNicknameException();
        }
        userProfileService.isPossibleNickname(nickname, authorizationHeader);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editProfile(@RequestHeader(TokenProvider.HEADER_AUTHORIZATION) String authorizationHeader,
                                            @PathVariable("user_id") Long userId,
                                            @Valid @RequestBody ProfileRequest request) {

        userProfileService.putUserProfile(authorizationHeader, userId, request);
        log.info("유저 id: {}의 프로필이 수정되었습니다.", userId);
    }

    @GetMapping(value = {"/profile/{user_id}/feedbacks/{grade}", "/profile/{user_id}/feedbacks"})
    @Operation(description = "유저 프로필 페이지 / 피드백 API")
    public ResponseEntity<UserProfileFeedbackResponse> getFeedbacks(@PathVariable("user_id") Long userId, @PathVariable(value = "grade", required = false) String grade) {
        UserProfileFeedbackResponse response = userProfileService.getUserProfileFeedbacks(userId, grade);
        return (response != null)
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/profile/feedbacks/hide")
    @Operation(description = "피드백 숨김/공개를 변경 하는 API")
    public String hideFeedbacks(@RequestHeader(TokenProvider.HEADER_AUTHORIZATION) String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "hideFeedbacks");
        return userProfileService.hideFeedbacks(userId);
    }

    @PutMapping("/profile/user/hide")
    @Operation(description = "유저 프로필을 숨김/공개를 변경 하는 API")
    public String hideProfile(@RequestHeader(TokenProvider.HEADER_AUTHORIZATION) String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "hideProfile");
        return userProfileService.hideProfile(userId);
    }

    @GetMapping("/profile/state")
    @Operation(description = "현재 사용자의 설정 상태를 반환하는 API")
    public UserSettingStateResponse getUserSetting(@RequestHeader(TokenProvider.HEADER_AUTHORIZATION) String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getUserSetting");
        return userProfileService.getUserSettingState(userId);
    }
}
