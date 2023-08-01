package com.example.matchup.matchupbackend.dto;

import lombok.Data;

@Data
public class TeamDetailResponse {
    private Long teamID;
    private String title;
    private String description;
    private Long leaderID;
}