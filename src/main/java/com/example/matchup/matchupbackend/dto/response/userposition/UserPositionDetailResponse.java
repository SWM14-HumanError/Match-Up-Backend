package com.example.matchup.matchupbackend.dto.response.userposition;

import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.global.RoleType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserPositionDetailResponse {

    private RoleType type;
    private Integer typeLevel;
    private List<String> tags;

    public static UserPositionDetailResponse fromEntity(UserPosition userPosition) {
        return UserPositionDetailResponse.builder()
                .type(userPosition.getType())
                .typeLevel(userPosition.getTypeLevel())
                .build();
    }
}
