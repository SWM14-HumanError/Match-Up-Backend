package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.TeamMentoring;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringApplyListResponse {

    private Long applyId;
    private Long teamId;
    private String teamName;
    private String teamImageUrl;
    private Long leaderId;
    private String phoneNumber;
    private String content;

    @Builder
    private MentoringApplyListResponse(Long applyId, Long teamId, String teamName, String teamImageUrl, Long leaderId, String phoneNumber, String content) {
        this.applyId = applyId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamImageUrl = teamImageUrl;
        this.leaderId = leaderId;
        this.phoneNumber = phoneNumber;
        this.content = content;
    }

    public static MentoringApplyListResponse of(TeamMentoring teamMentoring) {
        return MentoringApplyListResponse.builder()
                .applyId(teamMentoring.getId())
                .teamId(teamMentoring.getTeam().getId())
                .teamName(teamMentoring.getTeam().getTitle())
                .teamImageUrl(teamMentoring.getTeam().getThumbnailUrl())
                .leaderId(teamMentoring.getTeam().getLeaderID())
                .phoneNumber(teamMentoring.getPhoneNumber())
                .content(teamMentoring.getMessageFromLeader())
                .build();
    }
}
