package com.example.matchup.matchupbackend.dto.request.jobposting;

import com.example.matchup.matchupbackend.entity.JobPosition;
import com.example.matchup.matchupbackend.entity.JobType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobPostingSearchRequest {
    private String searchWord;
    private JobPosition jobPosition;
    private JobType jobType;
    private boolean hideClosedJob;
}
