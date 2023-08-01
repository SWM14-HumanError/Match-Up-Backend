package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "mentoring")
public class Mentoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentoring_id")
    private Long id;
    @Column(name = "mentoring_title")
    private String title;
    @Column(name = "mentoring_content")
    private String content;
    @Column(name = "thumbnail_url")
    private String thumbnailURL;
    @Column(name = "likes")
    private Long likes;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mentor")
    private User mentor;
    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<TeamMentoring> teamMentoringList = new ArrayList<>();
    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<Review> mentoringReviewList = new ArrayList<>();

    //== 비즈니스 로직 ==//
    public Double returnMentoringReviewAverage() {
        Double totalScore = 0.0;
        for (Review mentoringReview : mentoringReviewList) {
            totalScore += mentoringReview.getScore();
        }
        if (mentoringReviewList.size() == 0) return 0.0;
        return totalScore / mentoringReviewList.size();
    }

}
