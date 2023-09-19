package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.user.AdditionalUserInfoRequest;
import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.config.jwt.TokenService;
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
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @GetMapping("/list/user")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "메인 페이지 / 유저 API")
    public SliceUserCardResponse showUsers(UserSearchRequest userSearchRequest, Pageable pageable) {
        return userService.searchSliceUserCard(userSearchRequest, pageable);
    }

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
}
