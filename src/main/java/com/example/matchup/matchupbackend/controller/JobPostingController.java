package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingRequest;
import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingSearchRequest;
import com.example.matchup.matchupbackend.dto.response.jobposting.JobPostingDetailResponse;
import com.example.matchup.matchupbackend.dto.response.jobposting.JobPostingPageResponse;
import com.example.matchup.matchupbackend.entity.JobPosting;
import com.example.matchup.matchupbackend.entity.Role;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.AdminOnlyPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "채용 공고 목록을 검색 하는 API")
    public JobPostingPageResponse searchJobPosting(JobPostingSearchRequest jobPostingSearchRequest, Pageable pageable) {
        return jobPostingService.searchJobPosting(jobPostingSearchRequest, pageable);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "채용 공고를 등록 하는 API")
    public String createJobPosting(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @RequestBody JobPostingRequest jobPostingRequest) {
        User user = tokenProvider.getUser(authorizationHeader, "createJobPosting");
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new AdminOnlyPermitException("채용 공고는 관리자만 등록할 수 있습니다.");
        }
        JobPosting jobPosting = jobPostingService.saveJobPosting(jobPostingRequest);
        return "채용 공고 ID : " + jobPosting.getId();
    }

    @GetMapping("/{jobPostingId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "채용 공고 상세 정보를 조회 하는 API")
    public JobPostingDetailResponse getDetailJobPosting(@PathVariable Long jobPostingId) {
        return jobPostingService.getDetailJobPosting(jobPostingId);
    }

    @DeleteMapping("/{jobPostingId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "채용 공고를 삭제 하는 API")
    public String deleteJobPosting(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long jobPostingId) {
        User user = tokenProvider.getUser(authorizationHeader, "deleteJobPosting");
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new AdminOnlyPermitException("채용 공고는 관리자만 삭제할 수 있습니다.");
        }
        return jobPostingService.deleteJobPosting(jobPostingId);
    }
}
