package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingRequest;
import com.example.matchup.matchupbackend.entity.Role;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.AdminOnlyPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job-posting")
public class JobPostingController {

    private final TokenProvider tokenProvider;
    private final JobPostingService jobPostingService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "채용 공고를 등록 하는 API")
    public String createJobPosting(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @RequestBody JobPostingRequest jobPostingRequest) {
        User user = tokenProvider.getUser(authorizationHeader, "createJobPosting");
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new AdminOnlyPermitException("채용공고는 관리자만 등록할 수 있습니다.");
        }
        jobPostingService.saveJobPosting(jobPostingRequest);
        return jobPostingRequest.getCompanyName() + "의 채용 공고가 등록 되었습니다.";
    }
}
