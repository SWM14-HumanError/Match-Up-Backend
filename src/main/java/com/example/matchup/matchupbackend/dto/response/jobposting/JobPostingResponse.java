package com.example.matchup.matchupbackend.dto.response.jobposting;

import com.example.matchup.matchupbackend.entity.JobPosition;
import com.example.matchup.matchupbackend.entity.JobPosting;
import com.example.matchup.matchupbackend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingResponse {
    private Long id;
    private String companyName;
    private String title;
    private LocalDateTime deadLine;
    private String imgUrl;
    private JobPosition jobPosition;
    private JobType jobType;

    public static JobPostingResponse fromEntity(JobPosting jobPosting) {
        return JobPostingResponse.builder()
                .id(jobPosting.getId())
                .companyName(jobPosting.getCompanyName())
                .title(jobPosting.getTitle())
                .deadLine(jobPosting.getDeadLine())
                .imgUrl(jobPosting.getImgUrl())
                .jobPosition(jobPosting.getJobPosition())
                .jobType(jobPosting.getJobType())
                .build();
    }
}
