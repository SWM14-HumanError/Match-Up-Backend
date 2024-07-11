package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.entity.UserPosition;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Position {
    private String positionName;
    private Long level;
    @QueryProjection
    public Position(String positionName, Long level) {
        this.positionName = positionName;
        this.level = level;
    }

    public static Position from(UserPosition userPosition) {
        return new Position(userPosition.getType().getRole(), userPosition.getTypeLevel().longValue());
    }
    public static Position of(String positionName, Long level) {
        return new Position(positionName, level);
    }
}
