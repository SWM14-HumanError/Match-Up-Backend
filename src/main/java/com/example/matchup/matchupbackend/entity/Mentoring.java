package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.mentoring.CreateOrEditMentoringRequest;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mentoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentoring_id")
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(length = 700)
    private String content;

    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double score = 0.0;

    @Enumerated(EnumType.STRING)
    private RoleType roleType; // 직무

    @Enumerated(EnumType.STRING)
    private Career career; // 경력

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted = false;

    private String thumbnailUrl;

    private String thumbnailUploadUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<MentoringTag> mentoringTags = new ArrayList<>();

    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<TeamMentoring> teamMentoringList = new ArrayList<>();

    @OneToMany(mappedBy = "mentoring", cascade = CascadeType.ALL)
    private List<ReviewMentor> mentoringReviewListMentor = new ArrayList<>();

    @Builder
    private Mentoring(String title, String content, RoleType roleType, Career career, User mentor) {
        this.title = title;
        this.content = content;
        this.roleType = roleType;
        this.career = career;
        this.mentor = mentor;
        this.score = 0.0;
    }

    public static Mentoring create(CreateOrEditMentoringRequest request, User mentor) {
        Mentoring mentoring = Mentoring.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .roleType(request.getRoleType())
                .career(request.getCareer())
                .mentor(mentor)
                .build();

        mentoring.setMentoringTagWhenCreate(request);

        return mentoring;
    }

    private void setMentoringTagWhenCreate(CreateOrEditMentoringRequest request) {
        this.mentoringTags = request.getStacks().stream()
                .map(stack -> MentoringTag.builder()
                        .tagName(stack)
                        .mentoring(this)
                        .build())
                .toList();
    }

    public void edit(CreateOrEditMentoringRequest request, List<MentoringTag> mentoringTags) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.career = request.getCareer();
        this.roleType = request.getRoleType();
        this.mentoringTags = mentoringTags;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void setUploadFile(UploadFile uploadFile){
        this.thumbnailUploadUrl = uploadFile.getUploadFileName();
        this.thumbnailUrl = String.valueOf(uploadFile.getS3Url());
    }

    public void setScoreAfterReviewFromMentee(Double score) {
        this.score = score;
    }
}
