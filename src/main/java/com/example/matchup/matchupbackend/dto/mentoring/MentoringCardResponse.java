package com.example.matchup.matchupbackend.dto.mentoring;

import com.example.matchup.matchupbackend.dto.Position;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MentoringCardResponse {
    private Long mentoringID;
    private String thumbnailURL;
    private String title;
    private Position position;
    private String mentorProfileURL;
    private String mentorNickname;
    private Double score;
    private Long like;

    @QueryProjection
    @Builder
    public MentoringCardResponse(Long mentoringID, String thumbnailURL, String title, String positionName, String positionLevel, String mentorProfileURL, String mentorNickname, Double score, Long like) {
        this.mentoringID = mentoringID;
        this.thumbnailURL = thumbnailURL;
        this.title = title;
        this.position = new Position(positionName, positionLevel);
        this.mentorProfileURL = mentorProfileURL;
        this.mentorNickname = mentorNickname;
        this.score = score;
        this.like = like;
    }
}
