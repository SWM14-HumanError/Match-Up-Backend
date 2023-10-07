package com.example.matchup.matchupbackend.dto.response.teamuser;

import com.example.matchup.matchupbackend.dto.response.userposition.UserPositionDetailResponse;
import com.example.matchup.matchupbackend.entity.TeamRecruit;
import com.example.matchup.matchupbackend.entity.UserPosition;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class RecruitInfoResponse {
    // 유저 정보
    private Long userId;
    private String thumbnailUrl;
    private String userName;
    private List<UserPositionDetailResponse> userPosition;

    // 지원서
    private String applyRole;
    private String content;

    public static RecruitInfoResponse from(TeamRecruit recruit, List<UserPosition> userPosition) {
        List<UserPositionDetailResponse> userPositions = userPosition.stream()
                .map(UserPositionDetailResponse::fromEntity)
                .collect(Collectors.toList());

        return RecruitInfoResponse.builder()
                .userId(recruit.getUser().getId())
                .thumbnailUrl(recruit.getUser().getPictureUrl())
                .userName(recruit.getUser().getNickname())
                .userPosition(userPositions)
                .applyRole(recruit.getRole())
                .content(recruit.getContent())
                .build();
    }
}
