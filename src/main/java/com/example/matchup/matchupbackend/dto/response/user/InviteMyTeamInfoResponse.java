package com.example.matchup.matchupbackend.dto.response.user;

import com.example.matchup.matchupbackend.entity.Team;
import lombok.Builder;
import lombok.Data;

@Data
public class InviteMyTeamInfoResponse {

    private Long teamId;
    private Long teamType;
    private String teamTitle;

    @Builder
    public InviteMyTeamInfoResponse(Team team) {
        this.teamId = team.getId();
        this.teamType = team.getType();
        this.teamTitle = team.getTitle();
    }
}
