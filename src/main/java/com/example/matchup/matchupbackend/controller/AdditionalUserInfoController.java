package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.user.AdditionalUserInfoRequest;
import com.example.matchup.matchupbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AdditionalUserInfoController {

    private final UserService userService;

    @PutMapping("/login/user/info")
    public ResponseEntity<Long> additionalUserInfo(@RequestHeader(value = HEADER_AUTHORIZATION) String token,
                                                   @RequestBody AdditionalUserInfoRequest dto) {

        Long userId = userService.saveAdditionalUserInfo(token, dto);
        return (userId != null)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}