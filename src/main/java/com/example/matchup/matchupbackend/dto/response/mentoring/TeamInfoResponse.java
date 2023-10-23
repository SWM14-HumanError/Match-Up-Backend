package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.Team;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamInfoResponse {

    private Long teamId;

    private String title;

    @Builder
    private TeamInfoResponse(Long teamId, String title) {
        this.teamId = teamId;
        this.title = title;
    }

    public static TeamInfoResponse of(Team team) {
        return TeamInfoResponse.builder()
                .teamId(team.getId())
                .title(team.getTitle())
                .build();
    }
}
