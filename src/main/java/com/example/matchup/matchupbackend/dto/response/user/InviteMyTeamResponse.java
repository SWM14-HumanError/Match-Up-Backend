package com.example.matchup.matchupbackend.dto.response.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InviteMyTeamResponse {

    private List<InviteMyTeamInfoResponse> teams;

    @Builder
    public InviteMyTeamResponse(List<InviteMyTeamInfoResponse> response) {
        this.teams = response;
    }
}
