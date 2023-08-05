package com.example.matchup.matchupbackend.dto.user;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TechStack {
    private Long tagID;
    private String tagName;
    @QueryProjection
    @Builder
    public TechStack(Long tagID, String tagName) {
        this.tagID = tagID;
        this.tagName = tagName;
    }
}
