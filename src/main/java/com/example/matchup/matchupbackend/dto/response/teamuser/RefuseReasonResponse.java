package com.example.matchup.matchupbackend.dto.response.teamuser;

import lombok.Data;

import java.time.LocalDateTime;

@Data
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
}
