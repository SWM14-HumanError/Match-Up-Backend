package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.enterprise.EnterpriseVerifyFormRequest;
import com.example.matchup.matchupbackend.dto.response.enterprise.SliceEnterpriseApply;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.EnterpriseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
    public String postEnterpriseVerifyForm(@Valid @RequestBody EnterpriseVerifyFormRequest verifyFormRequest,
                                         @RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "postEnterpriseVerifyForm");
        User user = enterpriseService.applyVerifyEnterprise(userId, verifyFormRequest);
        log.info("사용자 {} 가 기업 인증 신청을 했습니다.", user.getEmail());
        return "기업 인증 신청 완료.";
    }

    @GetMapping("/verify/list")
    public SliceEnterpriseApply getEnterpriseVerifyList(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                                        Pageable pageable) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getEnterpriseVerifyList");
        return enterpriseService.showEnterpriseVerifyList(userId, pageable);
    }
}
