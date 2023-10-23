package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyMentoringRequest;
import com.example.matchup.matchupbackend.dto.response.mentoring.MentoringApplyListResponse;
import com.example.matchup.matchupbackend.dto.response.mentoring.TeamInfoResponse;
import com.example.matchup.matchupbackend.service.MentoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MentoringApplyController {

    private final MentoringService mentoringService;

    @GetMapping("/mentoring/apply")
    @ResponseStatus(HttpStatus.OK)
    public List<TeamInfoResponse> getApplyMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        return mentoringService.getApplyMentoringForm(authorizationHeader);
    }

    @PostMapping("/mentoring/{mentoringId}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public void postApplyMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                   @PathVariable Long mentoringId,
                                   @Valid @RequestBody ApplyMentoringRequest request) {
        mentoringService.applyMentoring(request, mentoringId, authorizationHeader);
    }

    @PostMapping("/mentoring/apply/{applyId}/accept")
    @ResponseStatus(HttpStatus.CREATED)
    public void postAcceptApplyMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                         @PathVariable Long applyId,
                                         @RequestParam String comment) {
        mentoringService.acceptApplyMentoring(applyId, comment, authorizationHeader);
    }

    @PostMapping("/mentoring/apply/{applyId}/refuse")
    @ResponseStatus(HttpStatus.CREATED)
    public void postRefuseApplyMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                         @PathVariable Long applyId,
                                         @RequestParam String comment) {
        mentoringService.refuseApplyMentoring(applyId, comment, authorizationHeader);
    }

    @GetMapping("/mentoring/apply/list")
    @ResponseStatus(HttpStatus.OK)
    public List<MentoringApplyListResponse> getApplyMentoringList(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        return mentoringService.showApplyMentoringList(authorizationHeader);
    }
}
