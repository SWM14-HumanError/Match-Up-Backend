package com.example.matchup.matchupbackend.dto.response.team;

import lombok.Builder;
import lombok.Data;

@Data
public class TeamLikeResponse {

    private Boolean check;
    private int totalLike;

    @Builder
    private TeamLikeResponse(Boolean check, int totalLike) {
        this.check = check;
        this.totalLike = totalLike;
    }
}
