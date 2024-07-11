package com.example.matchup.matchupbackend.dto.response.team;

import com.example.matchup.matchupbackend.dto.TechStack;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TeamSearchResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private List<TechStack> techStacks;
    private Long leaderID;
    private String leaderNickname;
    private Long leaderLevel;
    private Long isFinished;
    @Builder
    public TeamSearchResponse(Long id, String title, String description, String thumbnailUrl, List<TechStack> techStacks, Long leaderID, String leaderNickname, Long leaderLevel, Long isFinished) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.techStacks = techStacks;
        this.leaderID = leaderID;
        this.leaderNickname = leaderNickname;
        this.leaderLevel = leaderLevel;
        this.isFinished = isFinished;
    }

    public static TeamSearchResponse from(Team team, Map<Long, User> userMap) {
        TeamSearchResponse build = TeamSearchResponse
                .builder()
                .id(team.getId())
                .title(team.getTitle())
                .description(team.getDescription())
                .thumbnailUrl(team.getThumbnailUrl())
                .techStacks(team.returnStackList())
                .leaderID(team.getLeaderID())
                .leaderNickname(userMap.get(team.getLeaderID()).getNickname())
                .leaderLevel(userMap.get(team.getLeaderID()).getUserLevel())
                .isFinished(team.getIsFinished())
                .build();
        return build;
    }
}
