package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.mentoring.SliceMentoringCardResponse;
import com.example.matchup.matchupbackend.dto.request.mentoring.CreateOrEditMentoringRequest;
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
                              @Valid @RequestBody CreateOrEditMentoringRequest request) {
        mentoringService.createMentoring(authorizationHeader, request);
    }

    @PutMapping("/mentoring/{mentoringId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                             @Valid @RequestBody CreateOrEditMentoringRequest request,
                             @PathVariable Long mentoringId) {
        mentoringService.editMentoring(authorizationHeader, request, mentoringId);
    }

    @DeleteMapping("/mentoring/{mentoringId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                @PathVariable Long mentoringId) {
        mentoringService.deleteMentoring(authorizationHeader, mentoringId);
    }
}
