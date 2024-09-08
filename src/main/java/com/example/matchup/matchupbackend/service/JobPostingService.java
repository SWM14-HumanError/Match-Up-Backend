package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingRequest;
import com.example.matchup.matchupbackend.entity.JobPosting;
import com.example.matchup.matchupbackend.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobPostingService {
    private final JobPostingRepository jobPostingRepository;
    private final FileService fileService;

    @Transactional
    public void saveJobPosting(JobPostingRequest jobPostingRequest) {
        if(jobPostingRequest.getImageBase64() != null) {
            UploadFile uploadFile = fileService.storeBase64ToFile(jobPostingRequest.getImageBase64(), jobPostingRequest.getImageName());
            jobPostingRepository.save(JobPosting.from(uploadFile, jobPostingRequest));
        } else {
            jobPostingRepository.save(JobPosting.from(jobPostingRequest));
        }
    }
}
