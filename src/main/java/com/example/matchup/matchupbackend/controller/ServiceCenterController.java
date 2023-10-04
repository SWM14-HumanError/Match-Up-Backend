package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.servicecenter.OneOnOneInquiryRequest;
import com.example.matchup.matchupbackend.service.ServiceCenterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ServiceCenterController {

    private final ServiceCenterService serviceCenterService;

    @PostMapping("/inquiry")
    @ResponseStatus(HttpStatus.CREATED)
    public void receiveOneOnOneInquiry(@RequestHeader(HEADER_AUTHORIZATION) String authorizationHeader,
                                         @Valid @RequestBody OneOnOneInquiryRequest oneOnOneInquiryRequest) {
        serviceCenterService.postOneOnOneInquiry(authorizationHeader, oneOnOneInquiryRequest);
    }
}
