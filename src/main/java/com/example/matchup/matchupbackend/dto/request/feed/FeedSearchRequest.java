package com.example.matchup.matchupbackend.dto.request.feed;

import com.example.matchup.matchupbackend.dto.FeedSearchType;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedSearchRequest {

    @Enum(enumClass = FeedSearchType.class)
    private String searchType; // 작성자 혹은 피드 제목

    @Size(max = 50, message = "검색하려는 단어는 50글자를 넘길 수 없습니다.")
    private String searchValue;

    @Enum(enumClass = ProjectDomain.class)
    private String domain;

    public FeedSearchType getSearchType() {
        return FeedSearchType.valueOf(this.searchType);
    }

    public ProjectDomain getDomain() {
        return ProjectDomain.valueOf(this.domain);
    }
}

