package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingRequest;
import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingSearchRequest;
import com.example.matchup.matchupbackend.dto.response.jobposting.JobPostingDetailResponse;
import com.example.matchup.matchupbackend.dto.response.jobposting.JobPostingPageResponse;
import com.example.matchup.matchupbackend.dto.response.jobposting.JobPostingResponse;
import com.example.matchup.matchupbackend.entity.JobPosting;
import com.example.matchup.matchupbackend.repository.jobposting.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobPostingService {
    private final JobPostingRepository jobPostingRepository;
    private final FileService fileService;

    /**
     * 채용 공고를 검색한다.
     */
    public JobPostingPageResponse searchJobPosting(JobPostingSearchRequest jobPostingSearchRequest, Pageable pageable) {
        Page<JobPosting> jobPostingBySearchRequest = jobPostingRepository.findJobPostingBySearchRequest(jobPostingSearchRequest, pageable);
        List<JobPostingResponse> content = jobPostingBySearchRequest.getContent().stream()
                .map(JobPostingResponse::fromEntity)
                .collect(Collectors.toList());
        return JobPostingPageResponse.builder()
                .responseList(content)
                .page(jobPostingBySearchRequest.getNumber() + 1)
                .totalDataCount(jobPostingBySearchRequest.getTotalElements())
                .totalPageCount(jobPostingBySearchRequest.getTotalPages())
                .build();
    }

    /**
     * ADMIN이 채용 공고를 저장한다.
     */
    @Transactional
    public void saveJobPosting(JobPostingRequest jobPostingRequest) {
        if(jobPostingRequest.getImageBase64() != null) {
            UploadFile uploadFile = fileService.storeBase64ToFile(jobPostingRequest.getImageBase64(), jobPostingRequest.getImageName());
            jobPostingRepository.save(JobPosting.from(uploadFile, jobPostingRequest));
        } else {
            jobPostingRepository.save(JobPosting.from(jobPostingRequest));
        }
    }

    /**
     * 채용 공고 세부 사항을 조회 한다.
     */
    public JobPostingDetailResponse getDetailJobPosting(Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채용 공고 입니다."));
        return JobPostingDetailResponse.fromEntity(jobPosting);
    }
}
