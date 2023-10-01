package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.user.AdditionalUserInfoRequest;
import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/list/user")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "메인 페이지 / 유저 API")
    public SliceUserCardResponse showUsers(UserSearchRequest userSearchRequest, Pageable pageable) {
        return userService.searchSliceUserCard(userSearchRequest, pageable);
    }

    /**
     * 회원가입 후에 최소 정보를 받는다.
     * 서비스에 사용할 닉네임과 프로필 사진, 생년월일, 개발 연차
     */
    @PutMapping("/login/user/info")
    public ResponseEntity<Long> additionalUserInfo(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                                   @Valid @RequestBody AdditionalUserInfoRequest request) {

        Long userId = userService.saveAdditionalUserInfo(authorizationHeader, request);
        return (userId != null)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    /**
     * 액세스 토큰이 만료되었을 때, 리프레시 토큰을 이용해 액세스 토큰을 재발급
     * 이용약관에 동의하지 않은, 최초 로그인 유저는 이용약관 동의 페이지로 이동하기 위해서 signup query를 추가함
     * 관련 값은 tokenService.createNewAccessToken(token)에서 처리
     */
    @GetMapping("/login/token/refresh")
    @ResponseStatus(HttpStatus.OK)
    public String loginToken(HttpServletRequest request) {
        return userService.tokenRefresh(request);
    }

    /**
     * 유저가 이용약관에 동의
     */
    @GetMapping("/login/user/term")
    @ResponseStatus(HttpStatus.OK)
    public void userAgreeTermOfService(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        userService.userAgreeTermOfService(authorizationHeader);
    }

    /**
     * 유저가 온라인 상태를 지속하면 로그인 시간 최신화
     */
    @GetMapping("/user/online")
    @ResponseStatus(HttpStatus.OK)
    public void isUserOnline(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        userService.updateUserLastLogin(authorizationHeader);
    }
}
