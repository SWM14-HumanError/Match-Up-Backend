package com.example.matchup.matchupbackend.dto.response.teamuser;

import com.example.matchup.matchupbackend.dto.response.userposition.UserPositionDetailResponse;
import lombok.Data;

@Data
public class RecruitInfoResponse {
    // 유저 정보
    private String userId;
    private String thumbnailUrl;
    private String userName;
    private UserPositionDetailResponse userPosition;

    // 지원서
    private String applyRole;
    private String content;

}
