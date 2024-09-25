package com.example.matchup.matchupbackend.dto.response.jobposting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingPageResponse {
    private List<JobPostingResponse> responseList;
    private int page;
    private long totalDataCount;
    private int totalPageCount;
}
