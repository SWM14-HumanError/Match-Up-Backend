package com.example.matchup.matchupbackend.dto.response.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedSearchResponse {

    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private LocalDate createdDate;
    private String userName;
    private String userPictureUrl;
    private Long positionLevel;
    private Long userId;

    @Builder
    public FeedSearchResponse(Feed feed, User user) {
        this.id = feed.getId();
        this.title = feed.getTitle();
        this.content = feed.getContent();
        this.thumbnailUrl = feed.getThumbnailUrl();
        this.createdDate = feed.getCreateTime().toLocalDate();
        this.userName = user.getName();
        this.userPictureUrl = user.getPictureUrl();
        this.positionLevel = user.getPositionLevel();
        this.userId = user.getId();
    }
}
