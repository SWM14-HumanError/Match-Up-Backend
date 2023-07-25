package com.example.matchup.matchupbackend.dto;

import lombok.Data;

@Data
public class TeamSearchRequest {
    private Long type;
    private String category;
    private String search;

    private int page;

    private int size;
}
