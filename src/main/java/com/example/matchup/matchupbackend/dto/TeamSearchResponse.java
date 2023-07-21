package com.example.matchup.matchupbackend.dto;

import lombok.Data;

@Data
public class TeamSearchResponse {
    private Long id;
    private String title;
    private String description;
    private Long like;
    private String thumbnailUrl;
}
