package com.example.matchup.matchupbackend.dto;

import lombok.Data;

@Data
public class TeamType {
    private Long teamType; //project or study
    private String detailType; //세부 타입 설정
}
