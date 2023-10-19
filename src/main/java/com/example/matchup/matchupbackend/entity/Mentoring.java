package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.mentoring.CreateMentoringRequest;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import lombok.Builder;
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

//    private String thumbnailUrl;
//    private String thumbnailUploadUrl;

    // UserPosition RoleType 중에 하나로 선택
    @Enumerated(EnumType.STRING)
    private RoleType roleType; // 직무

    @Enumerated(EnumType.STRING)
    private Career career; // 경력

//    @Column(name = "likes")
//    private Long likes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @OneToMany(mappedBy = "mentoring")
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<TeamMentoring> teamMentoringList = new ArrayList<>();

    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<Review> mentoringReviewList = new ArrayList<>();

    //== 비즈니스 로직 ==//
    public Double returnMentoringReviewAverage() {
        Double totalScore = 0.0;
        for (Review mentoringReview : mentoringReviewList) {
            totalScore += mentoringReview.getScore(); //todo 멘토의 score는 일반 유저의 온도와 다름
        }
        if (mentoringReviewList.size() == 0) return 0.0;
        return totalScore / mentoringReviewList.size();
    }

    @Builder
    private Mentoring(String title, String content, RoleType roleType, Career career, User mentor, List<Likes> likes, List<TeamMentoring> teamMentoringList, List<Review> mentoringReviewList) {
        this.title = title;
        this.content = content;
        this.roleType = roleType;
        this.career = career;
        this.mentor = mentor;
        this.likes = likes;
        this.teamMentoringList = teamMentoringList;
        this.mentoringReviewList = mentoringReviewList;
    }

    public static Mentoring create(CreateMentoringRequest request, User mentor) {
        return Mentoring.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .roleType(request.getRoleType())
                .career(request.getCareer())
                .mentor(mentor)
                .build();
    }
}
