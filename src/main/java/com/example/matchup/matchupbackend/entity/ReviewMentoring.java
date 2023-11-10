package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.mentoring.ReviewMentoringRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewMentoring {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_mentor_id")
    private Long id;

    private String comment;

    private Double satisfaction;

    private Double expertise;

    private Double punctuality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentoring_id", nullable = false)
    private Mentoring mentoring;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_mentoring_id", nullable = false)
    private TeamMentoring teamMentoring;

    @Builder
    public ReviewMentoring(String comment, Double satisfaction, Double expertise, Double punctuality, User mentee, Mentoring mentoring, TeamMentoring teamMentoring) {
        this.comment = comment;
        this.satisfaction = satisfaction;
        this.expertise = expertise;
        this.punctuality = punctuality;
        this.mentee = mentee;
        this.mentoring = mentoring;
        this.teamMentoring = teamMentoring;
    }

    public static ReviewMentoring create(ReviewMentoringRequest request, Mentoring mentoring, TeamMentoring teamMentoring, User mentee) {
        return ReviewMentoring.builder()
                .satisfaction(request.getSatisfaction())
                .expertise(request.getExpertise())
                .punctuality(request.getPunctuality())
                .comment(request.getComment())
                .mentee(mentee)
                .mentoring(mentoring)
                .teamMentoring(teamMentoring)
                .build();
    }
}