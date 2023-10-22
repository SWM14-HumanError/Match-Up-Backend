package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyVerifyMentorRequest;
import com.example.matchup.matchupbackend.dto.response.mentoring.VerifyMentorsResponse;
import com.example.matchup.matchupbackend.dto.response.mentoring.VerifyMentorsSliceResponse;
import com.example.matchup.matchupbackend.service.MentoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MentorVerifyController {

    private final MentoringService mentoringService;

    @PostMapping("/mentoring/verify")
    @ResponseStatus(HttpStatus.CREATED)
    public void postVerifyMentor(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                 @Valid @RequestBody ApplyVerifyMentorRequest request) {
        mentoringService.verifyMentor(request, authorizationHeader);
    }

    @GetMapping("/mentoring/verify/list")
    @ResponseStatus(HttpStatus.OK)
    public VerifyMentorsSliceResponse getVerifyMentors(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                                       Pageable pageable) {
        return mentoringService.showVerifyMentors(authorizationHeader, pageable);
    }

    @PostMapping("/mentoring/verify/{verifyId}/accept")
    @ResponseStatus(HttpStatus.CREATED)
    public void postAcceptVerifyMentors(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                        @PathVariable Long verifyId) {
        mentoringService.acceptVerifyMentors(authorizationHeader, verifyId);
    }

    @PostMapping("/mentoring/verify/{verifyId}/refuse")
    @ResponseStatus(HttpStatus.CREATED)
    public void postRefuseVerifyMentors(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                        @PathVariable Long verifyId) {
        mentoringService.refuseVerifyMentors(authorizationHeader, verifyId);
    }

    @GetMapping("/mentoring/verify")
    @ResponseStatus(HttpStatus.OK)
    public VerifyMentorsResponse getVerifyMentorEditForm(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        return mentoringService.showVerifyMentorEditForm(authorizationHeader);
    }

    @PutMapping("/mentoring/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putVerifyMentor(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                @Valid @RequestBody ApplyVerifyMentorRequest request) {
        mentoringService.editVerifyMentor(request, authorizationHeader);
    }
}
