package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.entity.UserPosition;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Position {
    private String positionName;
    private Long level; //팀원은 초보,중수,고수로 멘토는 대학생, 카카오 이런식으로 나눔
    @QueryProjection
    public Position(String positionName, Long level) {
        this.positionName = positionName;
        this.level = level;
    }

    public static Position from(UserPosition userPosition) {
        return new Position(userPosition.getType().getRole(), userPosition.getTypeLevel().longValue());
    }
}
