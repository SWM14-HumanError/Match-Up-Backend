package com.example.matchup.matchupbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class TeamType {
    private Long teamType; //project or study
    private String detailType; //세부 타입 설정
    @Builder
    public TeamType(Long teamType, String detailType) {
        this.teamType = teamType;
        this.detailType = detailType;
    }
}
