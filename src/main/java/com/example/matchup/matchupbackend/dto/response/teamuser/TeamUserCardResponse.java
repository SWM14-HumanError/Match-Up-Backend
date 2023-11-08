package com.example.matchup.matchupbackend.dto.response.teamuser;

import com.example.matchup.matchupbackend.dto.TechStack;
import com.example.matchup.matchupbackend.dto.UserCardResponse;
import com.example.matchup.matchupbackend.entity.Feedback;
import com.example.matchup.matchupbackend.entity.TeamRecruit;
import com.example.matchup.matchupbackend.entity.TeamUser;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.global.RoleType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@SuperBuilder
public class TeamUserCardResponse extends UserCardResponse {

    private RoleType role;
    private Boolean approve; // 팀 가입 승인여부
    private Long recruitID;
    private LocalDateTime lastFeedbackAt; // 마지막 피드백 시간
    private LocalDateTime toFeedbackAt; // 피드백 해야 하는 시간

    @QueryProjection
    public TeamUserCardResponse(Long userID, String profileImageURL, Long memberLevel, String nickname, String positionName, Long positionLevel, Double score, Long like, List<TechStack> TechStacks, RoleType role, Boolean approve, Long recruitID, LocalDateTime lastFeedbackAt, LocalDateTime toFeedbackAt) {
        super(userID, profileImageURL, memberLevel, nickname, positionName, positionLevel, score, like, TechStacks);
        this.role = role;
        this.approve = approve;
        this.recruitID = recruitID;
        this.lastFeedbackAt = lastFeedbackAt;
        this.toFeedbackAt = toFeedbackAt;
    }

    public static TeamUserCardResponse fromEntity(TeamUser teamUser, UserPosition userPosition) {
        Optional<TeamRecruit> teamRecruit = Optional.ofNullable(teamUser.getTeamRecruit());
        return new TeamUserCardResponse(
                teamUser.getUser().getId(),
                teamUser.getUser().getPictureUrl(),
                teamUser.getUser().getUserLevel(),
                teamUser.getUser().getNickname(),
                userPosition != null ? userPosition.getType().getRole() : RoleType.NA.toString(),
                userPosition != null ? Long.valueOf(userPosition.getTypeLevel()) : 0,
                teamUser.getUser().getFeedbackScore(),
                teamUser.getUser().getLikes(),
                teamUser.getUser().returnStackList(),
                teamUser.getRole(),
                teamUser.getApprove(),
                teamRecruit.orElse(null) == null ? null : teamRecruit.get().getId(),
                null,
                null);
    }

    public static TeamUserCardResponse fromMap(TeamUser teamUser, Feedback feedback) {
        Optional<TeamRecruit> teamRecruit = Optional.ofNullable(teamUser.getTeamRecruit());
        return new TeamUserCardResponse(
                teamUser.getUser().getId(),
                teamUser.getUser().getPictureUrl(),
                teamUser.getUser().getUserLevel(),
                teamUser.getUser().getNickname(),
                teamUser.getUser().getPosition(),
                teamUser.getUser().getPositionLevel(),
                teamUser.getUser().getFeedbackScore(),
                teamUser.getUser().getLikes(),
                teamUser.getUser().returnStackList(),
                teamUser.getRole(),
                teamUser.getApprove(),
                teamRecruit.orElse(null) == null ? null : teamRecruit.get().getId(),
                feedback.getUpdateTime(),
                feedback.timeToFeedback());
    }
}
