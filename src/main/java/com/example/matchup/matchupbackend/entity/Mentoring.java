package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
    private List<Feedback> mentoringFeedbackList = new ArrayList<>();

    //== 비즈니스 로직 ==//
//    public Double returnMentoringReviewAverage() {
//        Double totalScore = 0.0;
//        for (Feedback mentoringFeedback : mentoringFeedbackList) {
//            totalScore += mentoringFeedback.getScore();
//        }
//        if (mentoringFeedbackList.size() == 0) return 0.0;
//        return totalScore / mentoringFeedbackList.size();
//    }

}
