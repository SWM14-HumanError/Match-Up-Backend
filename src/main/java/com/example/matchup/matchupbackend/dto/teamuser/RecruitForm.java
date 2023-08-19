package com.example.matchup.matchupbackend.dto.teamuser;

import lombok.Data;

@Data
public class RecruitForm {
    private Long userID;
    private String userName;
    private String role;
    private String content;
}
