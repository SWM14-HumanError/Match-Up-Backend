package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.Career;
import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.MentoringTag;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.global.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MentoringSearchResponse {

    private String thumbnailUrl;

    private Long mentoringId;

    private String title;

    private String content;

    private RoleType roleType;

    private Career career;

    private Long likes;

    private Long stars;

    private String nickname;

    private Long userLevel;

    private String userPictureUrl;

    private Boolean likeMentoring;

    private List<String> stacks;

    @Builder
    private MentoringSearchResponse(String thumbnailUrl, Long mentoringId, String title, String content, RoleType roleType, Career career, Long likes, Long stars, String nickname, Long userLevel, String userPictureUrl, Boolean likeMentoring, List<String> stacks) {
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
        this.likeMentoring = likeMentoring;
        this.stacks = stacks;
    }

    public static MentoringSearchResponse ofSearch(Mentoring mentoring, Long likes, Long stars, Boolean likeMentoring) {
        User mentor = mentoring.getMentor();

        return MentoringSearchResponse.builder()
                .thumbnailUrl(mentoring.getThumbnailUrl())
                .mentoringId(mentoring.getId())
                .title(mentoring.getTitle())
                .roleType(mentoring.getRoleType())
                .career(mentoring.getCareer())
                .likes(likes)
                .stars(stars)
                .nickname(mentor.getNickname())
                .userLevel(mentor.getUserLevel())
                .userPictureUrl(mentor.getPictureUrl())
                .likeMentoring(likeMentoring)
                .build();
    }

    public static MentoringSearchResponse ofDetail(Mentoring mentoring, Long likes, Long stars) {
        User mentor = mentoring.getMentor();

        return MentoringSearchResponse.builder()
                .thumbnailUrl(mentoring.getThumbnailUrl())
                .mentoringId(mentoring.getId())
                .title(mentoring.getTitle())
                .content(mentoring.getContent())
                .roleType(mentoring.getRoleType())
                .career(mentoring.getCareer())
                .likes(likes)
                .stars(stars)
                .nickname(mentor.getNickname())
                .userLevel(mentor.getUserLevel())
                .userPictureUrl(mentor.getPictureUrl())
                .stacks(mentoring.getMentoringTags().stream().map(MentoringTag::getTagName).toList())
                .build();
    }
}
