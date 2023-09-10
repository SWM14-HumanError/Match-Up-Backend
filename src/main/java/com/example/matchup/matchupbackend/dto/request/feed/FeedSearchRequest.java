package com.example.matchup.matchupbackend.dto.request.feed;

import com.example.matchup.matchupbackend.dto.FeedSearchType;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import lombok.Data;

@Data
public class FeedSearchRequest {

    private FeedSearchType searchType; // 작성자 혹은 피드 제목
    private String searchValue;
    private ProjectDomain domain;

    public FeedSearchRequest(FeedSearchType searchType, String searchValue, ProjectDomain domain) {
        this.searchType = searchType;
        this.searchValue = searchValue;
        this.domain = (domain == null) ? ProjectDomain.전체 : domain;
    }
}

