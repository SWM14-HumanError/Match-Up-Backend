package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyVerifyMentorRequest;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentorVerify {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentor_verify_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    private Career career;

    private String content;

    private String link;

    private String thumbnailUploadUrl;

    private String thumbnailUrl; //storeURL

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Builder
    private MentorVerify(RoleType roleType, Career career, String content, String link, String thumbnailUploadUrl, String thumbnailUrl, User user) {
        this.roleType = roleType;
        this.career = career;
        this.content = content;
        this.link = link;
        this.thumbnailUploadUrl = thumbnailUploadUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.user = user;
    }

    public static MentorVerify create(ApplyVerifyMentorRequest request, User user) {
        return MentorVerify.builder()
                .roleType(request.getRoleType())
                .career(request.getCareer())
                .content(request.getContent())
                .link(request.getLink())
                .user(user)
                .build();
    }

    public void edit(ApplyVerifyMentorRequest request) {
        this.roleType = request.getRoleType();
        this.career = request.getCareer();
        this.content = request.getContent();
        this.link = request.getLink();
    }

    public void setUploadFile(UploadFile uploadFile){
        this.thumbnailUploadUrl = uploadFile.getUploadFileName();
        this.thumbnailUrl = String.valueOf(uploadFile.getS3Url());
    }
}
