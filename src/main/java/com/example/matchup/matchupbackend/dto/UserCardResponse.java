package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class UserCardResponse{
    private Long userID;
    private String profileImageURL;
    private Long memberLevel;
    private String nickname;
    private Position position;
    private Double score;
    private Long like;
    private List<TechStack> techStacks;

    @QueryProjection
    public UserCardResponse(Long userID, String profileImageURL, Long memberLevel, String nickname, String positionName, Long positionLevel, Double score, Long like, List<TechStack> TechStacks) {
        this.userID = userID;
        this.profileImageURL = profileImageURL;
        this.memberLevel = memberLevel;
        this.nickname = nickname;
        this.position = new Position(positionName, positionLevel);
        this.score = score;
        this.like = like;
        this.techStacks = TechStacks;
    }

    public static UserCardResponse of(Long userID, String profileImageURL, Long memberLevel, String nickname, Position position, Double score, Long like, List<TechStack> TechStacks) {
        UserCardResponse build = UserCardResponse.builder()
                .userID(userID)
                .profileImageURL(profileImageURL)
                .memberLevel(memberLevel)
                .nickname(nickname)
                .position(position)
                .score(score)
                .like(like)
                .techStacks(TechStacks)
                .build();
        return build;
    }

    public static UserCardResponse fromEntity(User user, UserPosition userPosition) {
        Position position = new Position(userPosition.getType().toString(), Long.valueOf(userPosition.getTypeLevel()));

        return UserCardResponse.builder()
                .userID(user.getId())
                .profileImageURL(user.getPictureUrl())
                .memberLevel(user.getUserLevel())
                .nickname(user.getNickname())
                .position(position)
                .score(user.getFeedbackScore())
                .like(user.getLikes())
                .techStacks(user.returnStackList())
                .build();

    }
}
