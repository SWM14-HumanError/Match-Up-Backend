package com.example.matchup.matchupbackend.dto.mentoring;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.entity.Mentoring;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MentoringCardResponse {

    private Long mentoringID;
    private String title;
    private Position position;
    private String mentorProfileURL;
    private String mentorNickname;
    private Double score;
    private Long like;

    private String thumbnailURL;

    @QueryProjection
    @Builder
    public MentoringCardResponse(Long mentoringID, String title, String positionName, Long positionLevel, String mentorProfileURL, String mentorNickname, Double score) {
        this.mentoringID = mentoringID;
        this.title = title;
        this.position = new Position(positionName, positionLevel);
        this.mentorProfileURL = mentorProfileURL;
        this.mentorNickname = mentorNickname;
        this.score = score;
    }

    public static MentoringCardResponse fromEntity(Mentoring mentoring) {
        return new MentoringCardResponse(
                mentoring.getId(),
                mentoring.getTitle(),
                mentoring.getMentor().getPosition(),
                mentoring.getMentor().getPositionLevel(),
                mentoring.getMentor().getPictureUrl(),
                mentoring.getMentor().getName(),
                mentoring.getScore()
        );
    }
}
