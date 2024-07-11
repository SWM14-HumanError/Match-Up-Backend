package com.example.matchup.matchupbackend.dto.response.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import com.example.matchup.matchupbackend.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedSearchResponse {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String thumbnailUrl;
    private LocalDate createdDate;
    private String nickname;
    private String userPictureUrl;
    private Long positionLevel;
    private Boolean isLiked;
    private Long type;
    private ProjectDomain projectDomain;

    @Builder
    public FeedSearchResponse(Feed feed, User user) {
        this.id = feed.getId();
        this.title = feed.getTitle();
        this.content = feed.getContent();
        this.thumbnailUrl = feed.getThumbnailUrl();
        this.createdDate = feed.getCreateTime().toLocalDate();
        this.nickname = user.getNickname();
        this.userPictureUrl = user.getPictureUrl();
        this.positionLevel = user.getUserLevel();
        this.userId = user.getId();
        this.type = feed.getType();
        this.projectDomain = feed.getProjectDomain();
    }
}
