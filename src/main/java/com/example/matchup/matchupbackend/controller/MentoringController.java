package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.mentoring.CreateOrEditMentoringRequest;
import com.example.matchup.matchupbackend.dto.request.mentoring.MentoringSearchParam;
import com.example.matchup.matchupbackend.dto.response.mentoring.MentoringSearchResponse;
import com.example.matchup.matchupbackend.dto.response.mentoring.MentoringSliceResponse;
import com.example.matchup.matchupbackend.service.MentoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MentoringController {

    private final MentoringService mentoringService;

    @GetMapping("/mentorings")
    @ResponseStatus(HttpStatus.OK)
    public MentoringSliceResponse getMentoring(@RequestHeader(value = HEADER_AUTHORIZATION, required = false) String authorizationHeader,
                                               MentoringSearchParam param,
                                               @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return mentoringService.showMentoringsInMentoringPage(authorizationHeader, param, pageable);
    }

    @PostMapping("/mentoring")
    @ResponseStatus(HttpStatus.CREATED)
    public void postMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                              @Valid @RequestBody CreateOrEditMentoringRequest request) {
        mentoringService.createMentoringByMentor(authorizationHeader, request);
    }

    @PutMapping("/mentoring/{mentoringId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                             @Valid @RequestBody CreateOrEditMentoringRequest request,
                             @PathVariable Long mentoringId) {
        mentoringService.editMentoringByMentor(authorizationHeader, request, mentoringId);
    }

    @DeleteMapping("/mentoring/{mentoringId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentoring(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                @PathVariable Long mentoringId) {
        mentoringService.deleteMentoringByMentor(authorizationHeader, mentoringId);
    }

    @GetMapping("/mentoring/{mentoringId}")
    @ResponseStatus(HttpStatus.OK)
    public MentoringSearchResponse getMentoringDetail(@PathVariable Long mentoringId) {
        return mentoringService.showMentoringDetail(mentoringId);
    }

    @PostMapping("/mentoring/{mentoringId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void postMentoringLike(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                  @PathVariable Long mentoringId) {
        mentoringService.pushLikeOfMentoring(authorizationHeader, mentoringId);
    }

    @DeleteMapping("/mentoring/{mentoringId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentoringLike(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                    @PathVariable Long mentoringId) {
        mentoringService.undoLikeOfMentoring(authorizationHeader, mentoringId);
    }
}
