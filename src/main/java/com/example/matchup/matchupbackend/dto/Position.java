package com.example.matchup.matchupbackend.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Position {
    private String positionName;
    private String level; //팀원은 초보,중수,고수로 멘토는 대학생, 카카오 이런식으로 나눔
    @QueryProjection
    public Position(String positionName, String level) {
        this.positionName = positionName;
        this.level = level;
    }

}