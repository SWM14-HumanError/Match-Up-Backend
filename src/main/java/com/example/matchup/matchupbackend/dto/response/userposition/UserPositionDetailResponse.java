package com.example.matchup.matchupbackend.dto.response.userposition;

import com.example.matchup.matchupbackend.entity.UserPosition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPositionDetailResponse {

    private String positionName;
    private Integer positionLevel;

    public static UserPositionDetailResponse fromEntity(UserPosition userPosition) {
        return UserPositionDetailResponse.builder()
                .positionName(userPosition.getPositionName())
                .positionLevel(userPosition.getPositionLevel())
                .build();
    }
}
