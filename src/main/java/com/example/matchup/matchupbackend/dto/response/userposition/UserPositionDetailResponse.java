package com.example.matchup.matchupbackend.dto.response.userposition;

import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.global.RoleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPositionDetailResponse {

    private RoleType type;
    private Integer typeLevel;

    public static UserPositionDetailResponse fromEntity(UserPosition userPosition) {
        return UserPositionDetailResponse.builder()
                .type(userPosition.getType())
                .typeLevel(userPosition.getTypeLevel())
                .build();
    }
}
