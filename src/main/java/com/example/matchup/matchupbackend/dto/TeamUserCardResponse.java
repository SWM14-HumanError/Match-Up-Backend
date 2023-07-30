package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TeamUserCardResponse extends UserCardResponse {
    private String role;
    private Boolean approve; // 팀 가입 승인여부
    @QueryProjection
    @Builder
    public TeamUserCardResponse(Long userID, String profileImageURL, String memberLevel, String nickname, String positionName, String positionLevel, Double score, Long like, List<String> stacks, String role, Boolean approve) {
        super(userID, profileImageURL, memberLevel, nickname, positionName, positionLevel, score, like, stacks);
        this.role = role;
        this.approve = approve;
    }
}
