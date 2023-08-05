package com.example.matchup.matchupbackend.dto.user;

import com.example.matchup.matchupbackend.dto.Position;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
public class UserCardResponse{
    private Long userID;
    private String profileImageURL;
    private String memberLevel;
    private String nickname;
    private Position position;
    private Double score;
    private Long like;
    private List<TechStack> TechStacks;

    @QueryProjection
    public UserCardResponse(Long userID, String profileImageURL, String memberLevel, String nickname, String positionName, String positionLevel, Double score, Long like, List<TechStack> TechStacks) {
        this.userID = userID;
        this.profileImageURL = profileImageURL;
        this.memberLevel = memberLevel;
        this.nickname = nickname;
        this.position = new Position(positionName, positionLevel);
        this.score = score;
        this.like = like;
        this.TechStacks = TechStacks;
    }
}
