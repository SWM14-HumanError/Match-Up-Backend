package com.example.matchup.matchupbackend.dto.response.userposition;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPositionDetailResponse {

    private String positionName;
    private Integer positionLevel;
}
