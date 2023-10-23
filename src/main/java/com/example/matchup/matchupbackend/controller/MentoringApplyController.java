package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyMentoringRequest;
import com.example.matchup.matchupbackend.dto.response.mentoring.MentoringApplyListResponse;
import com.example.matchup.matchupbackend.dto.response.mentoring.TeamInfoResponse;
import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
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
    public List<TeamInfoResponse> getApplyMentoringInputForm(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        return mentoringService.getApplyMentoringInpuForm(authorizationHeader);
    }

    @PostMapping("/mentoring/{mentoringId}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public void postApplyMentoringByLeader(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                           @PathVariable Long mentoringId,
                                           @Valid @RequestBody ApplyMentoringRequest request) {
        mentoringService.applyMentoringByLeader(request, mentoringId, authorizationHeader);
    }

    @PostMapping("/mentoring/apply/{applyId}/accept")
    @ResponseStatus(HttpStatus.CREATED)
    public void postAcceptApplyMentoringByMentor(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                                 @PathVariable Long applyId,
                                                 @RequestParam String comment) {
        if (comment.length() > 20) {
            throw new ResourceNotPermitException(ErrorCode.NOT_PERMITTED, "멘토가 전하는 말은 20글자를 넘길 수 없습니다.");
        }
        mentoringService.acceptApplyMentoringByMentor(applyId, comment, authorizationHeader);
    }

    @PostMapping("/mentoring/apply/{applyId}/refuse")
    @ResponseStatus(HttpStatus.CREATED)
    public void postRefuseApplyMentoringByMentor(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader,
                                                 @PathVariable Long applyId,
                                                 @RequestParam String comment) {
        if (comment.length() > 20) {
            throw new ResourceNotPermitException(ErrorCode.NOT_PERMITTED, "멘토가 전하는 말은 20글자를 넘길 수 없습니다.");
        }
        mentoringService.refuseApplyMentoringByMentor(applyId, comment, authorizationHeader);
    }

    @GetMapping("/mentoring/apply/list")
    @ResponseStatus(HttpStatus.OK)
    public List<MentoringApplyListResponse> getApplyMentoringList(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader) {
        return mentoringService.showApplyMentoringList(authorizationHeader);
    }
}
