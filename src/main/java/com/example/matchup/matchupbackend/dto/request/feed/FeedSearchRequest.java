package com.example.matchup.matchupbackend.dto.request.feed;

import com.example.matchup.matchupbackend.dto.FeedSearchType;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import lombok.Data;

@Data
public class FeedSearchRequest {

    private FeedSearchType feedSearchType; // 작성자 혹은 피드 제목
    private String searchValue;
    private ProjectDomain domain;

    public FeedSearchRequest(FeedSearchType feedSearchType, String searchValue, ProjectDomain domain) {
        this.feedSearchType = feedSearchType;
        this.searchValue = searchValue;
        this.domain = (domain == null) ? ProjectDomain.전체 : domain;
    }
}

