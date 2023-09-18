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

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/alert")
@RequiredArgsConstructor
@Slf4j
public class AlertController {
    private final AlertService alertService;
    private final TokenProvider tokenProvider;

    @GetMapping("/")
    @Operation(description = "알림 리스트 조회")
    @ResponseStatus(value = HttpStatus.OK)
    public SliceAlertResponse getSliceAlertResponse(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @RequestBody AlertFilterRequest alertRequest, Pageable pageable) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getAlert");
        return alertService.getSliceAlertResponse(userId, alertRequest, pageable);
    }

    @PostMapping("/read/{alertId}")
    @Operation(description = "알림 읽음 표시")
    @ResponseStatus(value = HttpStatus.OK)
    public String setAlertStatusRead(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long alertId) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "readAlert");
        alertService.setAlertStatusRead(alertId, userId);
        return "alertId: " + alertId + " - 읽어졌습니다.";
    }


    //todo 알림 삭제는 204
    @PostMapping("/delete/{alertId}")
    @Operation(description = "알림 삭제")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Long
}
