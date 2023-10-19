package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.mentoring.SliceMentoringCardResponse;
import com.example.matchup.matchupbackend.dto.request.mentoring.CreateMentoringRequest;
import com.example.matchup.matchupbackend.service.MentoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MentoringController {

    private final MentoringService mentoringService;

    @GetMapping("/mentorings")
    @ResponseStatus(HttpStatus.OK)
    public SliceMentoringCardResponse getMentoring(Pageable pageable) {
        return mentoringService.showMentorings(pageable);
    }

    @PostMapping("/mentoring")
    @ResponseStatus(HttpStatus.CREATED)
    public void postMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                              @Valid @RequestBody CreateMentoringRequest request) {
        mentoringService.createMentoring(authorizationHeader, request);
    }
}
