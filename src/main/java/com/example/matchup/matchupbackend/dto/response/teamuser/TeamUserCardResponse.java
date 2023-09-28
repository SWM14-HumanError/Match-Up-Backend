package com.example.matchup.matchupbackend.dto.response.teamuser;

import com.example.matchup.matchupbackend.dto.TechStack;
import com.example.matchup.matchupbackend.dto.UserCardResponse;
import com.example.matchup.matchupbackend.entity.TeamRecruit;
import com.example.matchup.matchupbackend.entity.TeamUser;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@SuperBuilder
public class TeamUserCardResponse extends UserCardResponse {
    private String role;
    private Boolean approve; // 팀 가입 승인여부
    private Long recruitID;

    @QueryProjection
    public TeamUserCardResponse(Long userID, String profileImageURL, Long memberLevel, String nickname, String positionName, Long positionLevel, Double score, Long like, List<TechStack> TechStacks, String role, Boolean approve, Long recruitID) {
        super(userID, profileImageURL, memberLevel, nickname, positionName, positionLevel, score, like, TechStacks);
        this.role = role;
        this.approve = approve;
        this.recruitID = recruitID;
    }

    public static TeamUserCardResponse fromEntity(TeamUser teamUser) {
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
                teamRecruit.orElse(null) == null ? null : teamRecruit.get().getId());
    }
}
