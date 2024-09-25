package com.example.matchup.matchupbackend.repository.jobposting;

import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingSearchRequest;
import com.example.matchup.matchupbackend.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostingCustom {
    Page<JobPosting> findJobPostingBySearchRequest(JobPostingSearchRequest jobSearchRequest, Pageable pageable);
    Long countAll(JobPostingSearchRequest jobSearchRequest);
}
