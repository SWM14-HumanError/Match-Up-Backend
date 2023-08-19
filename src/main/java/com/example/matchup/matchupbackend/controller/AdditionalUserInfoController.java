package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.AdditionalUserInfoRequestDto;
import com.example.matchup.matchupbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AdditionalUserInfoController {

    private final UserService userService;

    @PutMapping("/login/user/info")
    public ResponseEntity<Long> additionalUserInfo(@RequestHeader(value = "Authorization") String token,
                                                   @RequestBody AdditionalUserInfoRequestDto dto) {

        Long userId = userService.saveAdditionalUserInfo(token, dto);
        return (userId != null)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}