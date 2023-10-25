package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.global.RoleType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@NoArgsConstructor
public class MentoringSearchResponse {

    private String thumbnailUrl;
    private Long mentoringId;
    private String title;
    private String content;
    private RoleType roleType;
    private Career career;
    private Long likes;
    private Double stars;
    private String nickname;
    private Long userLevel;
    private String userPictureUrl;
    private Boolean likeMentoring;
    private List<String> stacks;
    private Boolean availableReview;
    private ApplyStatus status;
    private String teamTitle;
    private Long teamId;
    private Long teamMentoringId;

    @Builder
    private MentoringSearchResponse(String thumbnailUrl, Long mentoringId, String title, String content, RoleType roleType, Career career, Long likes, Double stars, String nickname, Long userLevel, String userPictureUrl) {
        this.thumbnailUrl = thumbnailUrl;
        this.mentoringId = mentoringId;
        this.title = title;
        this.content = content;
        this.roleType = roleType;
        this.career = career;
        this.likes = likes;
        this.stars = stars;
        this.nickname = nickname;
        this.userLevel = userLevel;
        this.userPictureUrl = userPictureUrl;
    }

    private static MentoringSearchResponse of(Mentoring mentoring, Long likes) {
        User mentor = mentoring.getMentor();

        return MentoringSearchResponse.builder()
                .thumbnailUrl(mentoring.getThumbnailUrl())
                .mentoringId(mentoring.getId())
                .title(mentoring.getTitle())
                .content(mentoring.getContent())
                .roleType(mentoring.getRoleType())
                .career(mentoring.getCareer())
                .likes(likes)
                .stars(BigDecimal.valueOf(mentoring.getScore()).setScale(1, RoundingMode.HALF_UP).doubleValue())
                .nickname(mentor.getNickname())
                .userLevel(mentor.getUserLevel())
                .userPictureUrl(mentor.getPictureUrl())
                .build();
    }

    public static MentoringSearchResponse ofSearch(Mentoring mentoring, Long likes, Boolean likeMentoring) {
        MentoringSearchResponse response = of(mentoring, likes);
        response.setLikeMentoring(likeMentoring);
        return response;
    }

    public static MentoringSearchResponse ofDetail(Mentoring mentoring, Long likes) {
        MentoringSearchResponse response = of(mentoring, likes);
        response.setStacks(mentoring.getMentoringTags().stream().map(MentoringTag::getTagName).toList());
        return response;
    }

    public static MentoringSearchResponse ofMentoringInTeamPage(Mentoring mentoring, Long likes, Boolean availableReview, Boolean likeMentoring) {
        MentoringSearchResponse response = of(mentoring, likes);
        response.setLikeMentoring(likeMentoring);
        response.setAvailableReview(availableReview);
        return response;
    }

    public static MentoringSearchResponse ofMentor(Mentoring mentoring, Long likes, Boolean likeMentoring, ApplyStatus status, Team team, Long teamMentoringId) {
        MentoringSearchResponse response = of(mentoring, likes);
        response.setLikeMentoring(likeMentoring);
        response.setStatus(status);
        response.setTeamTitle(team.getTitle());
        response.setTeamId(team.getId());
        response.setTeamMentoringId(teamMentoringId);
        return response;
    }
}
