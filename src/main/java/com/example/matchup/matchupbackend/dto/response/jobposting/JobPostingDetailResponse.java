package com.example.matchup.matchupbackend.dto.response.jobposting;

import com.example.matchup.matchupbackend.entity.JobPosition;
import com.example.matchup.matchupbackend.entity.JobPosting;
import com.example.matchup.matchupbackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDetailResponse {
    private String companyName;
    private String title;
    private String description;
    private String imgUrl;
    private String applyLink;
    private JobPosition jobPosition;
    private JobType jobType;

    public static JobPostingDetailResponse fromEntity(JobPosting jobPosting) {
        return JobPostingDetailResponse.builder()
                .companyName(jobPosting.getCompanyName())
                .title(jobPosting.getTitle())
                .description(jobPosting.getDescription())
                .imgUrl(jobPosting.getImgUrl())
                .applyLink(jobPosting.getApplyLink())
                .jobPosition(jobPosting.getJobPosition())
                .jobType(jobPosting.getJobType())
                .build();
    }
}
