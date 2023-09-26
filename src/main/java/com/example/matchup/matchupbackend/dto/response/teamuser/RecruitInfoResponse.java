package com.example.matchup.matchupbackend.dto.response.teamuser;

import com.example.matchup.matchupbackend.dto.response.userposition.UserPositionDetailResponse;
import com.example.matchup.matchupbackend.entity.TeamRecruit;
import com.example.matchup.matchupbackend.entity.UserPosition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruitInfoResponse {
    // 유저 정보
    private Long userId;
    private String thumbnailUrl;
    private String userName;
    private UserPositionDetailResponse userPosition;

    // 지원서
    private String applyRole;
    private String content;

    public static RecruitInfoResponse from(TeamRecruit recruit, UserPosition userPosition) {
        return RecruitInfoResponse.builder()
                .userId(recruit.getUser().getId())
                .thumbnailUrl(recruit.getUser().getPictureUrl())
                .userName(recruit.getUser().getName())
                .userPosition(UserPositionDetailResponse.fromEntity(userPosition))
                .applyRole(recruit.getRole())
                .content(recruit.getContent())
                .build();
    }
}
