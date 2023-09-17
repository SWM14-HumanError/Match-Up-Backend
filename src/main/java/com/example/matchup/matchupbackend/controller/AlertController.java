package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.alert.AlertFilterRequest;
import com.example.matchup.matchupbackend.dto.response.alert.SliceAlertResponse;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.AlertService;
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
public class AlertController {
    private final AlertService alertService;
    private final TokenProvider tokenProvider;

    @GetMapping("/alert")
    @Operation(description = "알림 리스트 조회")
    @ResponseStatus(value = HttpStatus.OK)
    public SliceAlertResponse getSliceAlertResponse(@RequestHeader(value = "Authorization") String authorizationHeader, @RequestBody AlertFilterRequest alertRequest, Pageable pageable) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getAlert");
        return alertService.getSliceAlertResponse(userId, alertRequest, pageable);
    }

}
