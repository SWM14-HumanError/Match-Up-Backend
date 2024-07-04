package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.enterprise.EnterpriseStateRequest;
import com.example.matchup.matchupbackend.dto.request.enterprise.EnterpriseVerifyFormRequest;
import com.example.matchup.matchupbackend.dto.response.enterprise.SliceEnterpriseApply;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RequestMapping(value = "/api/v1/enterprise")
@RequiredArgsConstructor
@RestController
public class EnterpriseController {

    private final TokenProvider tokenProvider;
    private final EnterpriseService enterpriseService;

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "기업이 기업 인증을 신청 하는 API")
    public String postEnterpriseVerifyForm(@Valid @RequestBody EnterpriseVerifyFormRequest verifyFormRequest,
                                         @RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "postEnterpriseVerifyForm");
        User user = enterpriseService.applyVerifyEnterprise(userId, verifyFormRequest);
        log.info("사용자 {} 가 기업 인증 신청을 했습니다.", user.getEmail());
        return "기업 인증 신청 완료.";
    }

    @GetMapping("/verify/list")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "기업 인증 신청 목록을 조회 하는 API")
    public SliceEnterpriseApply getEnterpriseVerifyList(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                                        Pageable pageable) {
        Long adminId = tokenProvider.getUserId(authorizationHeader, "getEnterpriseVerifyList");
        return enterpriseService.showEnterpriseVerifyList(adminId, pageable);
    }

    @PostMapping("/verify/change")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "기업 인증 신청을 수락/거절 하는 API")
    public String changeEnterpriseVerifyState(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                         @RequestBody EnterpriseStateRequest enterpriseStateRequest) {
        Long adminId = tokenProvider.getUserId(authorizationHeader, "acceptEnterpriseVerify");
        User changedVerifyEnterprise = enterpriseService.changeEnterpriseVerifyState(adminId, enterpriseStateRequest);
        log.info("사용자 {} 의 기업 인증 상태가 {} 로 변경 되었습니다.", changedVerifyEnterprise.getEmail(), !enterpriseStateRequest.getIsAccepted());
        return enterpriseStateRequest.getIsAccepted() ? "기업 인증 취소 완료." : "기업 인증 승인 완료.";
    }
}
