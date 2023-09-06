package com.example.matchup.matchupbackend.dto.response.team;

import lombok.Data;

@Data
public class TeamDetailResponse {
    private Long teamID;
    private String title;
    private String description;
    private Long leaderID;
    private String thumbnailUrl;
}