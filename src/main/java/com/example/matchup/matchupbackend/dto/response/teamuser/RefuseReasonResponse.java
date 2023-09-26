package com.example.matchup.matchupbackend.dto.response.teamuser;

import com.example.matchup.matchupbackend.entity.TeamRefuse;
import com.example.matchup.matchupbackend.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RefuseReasonResponse {
    private Long leaderId;
    private String leaderImage;
    private String leaderName;
    private Long teamId;
    private String teamName;
    private String position;
    private String refusedUser;
    private String refuseReason;
    private LocalDateTime refuseDate;

    public static RefuseReasonResponse of(TeamRefuse teamRefuse, User teamLeader) {
        return RefuseReasonResponse.builder()
                .leaderId(teamRefuse.getTeam().getLeaderID())
                .leaderImage(teamLeader.getPictureUrl())
                .leaderName(teamLeader.getName())
                .teamId(teamRefuse.getTeam().getId())
                .teamName(teamRefuse.getTeam().getTitle())
                .position(teamRefuse.getPosition())
                .refusedUser(teamRefuse.getRefusedUser().getName())
                .refuseReason(teamRefuse.getRefuseReason())
                .refuseDate(teamRefuse.getCreateTime())
                .build();
    }
}
