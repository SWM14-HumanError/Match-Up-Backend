package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
