package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.dto.user.TechStack;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TeamSearchResponse {
    private Long id;
    private String title;
    private String description;
    private Long like;
    private String thumbnailUrl;
    private List<TechStack> techStacks;
    private Long leaderID;
    private String leaderName;
    private Long leaderLevel;
    @Builder
    public TeamSearchResponse(Long id, String title, String description, Long like, String thumbnailUrl, List<TechStack> techStacks, Long leaderID, String leaderName, Long leaderLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.like = like;
        this.thumbnailUrl = thumbnailUrl;
        this.techStacks = techStacks;
        this.leaderID = leaderID;
        this.leaderName = leaderName;
        this.leaderLevel = leaderLevel;
    }
}
