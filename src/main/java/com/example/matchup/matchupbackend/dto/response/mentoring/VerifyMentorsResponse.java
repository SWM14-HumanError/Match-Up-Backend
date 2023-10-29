package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.Career;
import com.example.matchup.matchupbackend.entity.MentorVerify;
import com.example.matchup.matchupbackend.global.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyMentorsResponse {

    private Long verifyId;

    private RoleType roleType;

    private Career career;

    private String content;

    private String link;

    private String thumbnailUrl;

    private Long userId;

    @Builder
    private VerifyMentorsResponse(Long verifyId, RoleType roleType, Career career, String content, String link, String thumbnailUrl, Long userId) {
        this.verifyId = verifyId;
        this.roleType = roleType;
        this.career = career;
        this.content = content;
        this.link = link;
        this.thumbnailUrl = thumbnailUrl;
        this.userId = userId;
    }

    public static VerifyMentorsResponse of(MentorVerify mentorVerify) {
        return VerifyMentorsResponse.builder()
                .verifyId(mentorVerify.getId())
                .roleType(mentorVerify.getRoleType())
                .career(mentorVerify.getCareer())
                .content(mentorVerify.getContent())
                .link(mentorVerify.getLink())
                .thumbnailUrl(mentorVerify.getThumbnailUrl())
                .userId(mentorVerify.getUser().getId())
                .build();
    }
}
