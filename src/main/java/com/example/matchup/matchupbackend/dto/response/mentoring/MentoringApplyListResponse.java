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
    private String phoneNumber;
    private String email;
    private String content;

    @Builder
    private MentoringApplyListResponse(Long applyId, Long teamId, String phoneNumber, String email, String content) {
        this.applyId = applyId;
        this.teamId = teamId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.content = content;
    }

    public static MentoringApplyListResponse of(TeamMentoring teamMentoring) {
        return MentoringApplyListResponse.builder()
                .applyId(teamMentoring.getId())
                .teamId(teamMentoring.getTeam().getId())
                .phoneNumber(teamMentoring.getPhoneNumber())
                .email(teamMentoring.getEmailOfTeamLeader())
                .content(teamMentoring.getMessageFromLeader())
                .build();
    }
}
