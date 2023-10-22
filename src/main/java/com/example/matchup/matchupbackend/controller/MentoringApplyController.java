package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyMentoringRequest;
import com.example.matchup.matchupbackend.service.MentoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MentoringApplyController {

    private final MentoringService mentoringService;

    @PostMapping("/mentoring/{mentoringId}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public void postApplyMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                   @PathVariable Long mentoringId,
                                   @Valid @RequestBody ApplyMentoringRequest request) {
        mentoringService.applyMentoring(request, mentoringId, authorizationHeader);
    }
}
