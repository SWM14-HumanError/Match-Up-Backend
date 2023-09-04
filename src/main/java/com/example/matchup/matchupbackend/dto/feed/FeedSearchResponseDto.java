package com.example.matchup.matchupbackend.dto.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedSearchResponseDto {

    private String title;
    private String content;
    private String thumbnailUrl;
    private LocalDate createdDate;
    private String userName;
    private String userPictureUrl;
    private Long positionLevel;

    @Builder
    public FeedSearchResponseDto(Feed feed, User user) {

        this.title = feed.getTitle();
        this.content = feed.getContent();
        this.thumbnailUrl = feed.getThumbnailUrl();
        this.createdDate = feed.getCreateTime().toLocalDate();
        this.userName = user.getName();
        this.userPictureUrl = user.getPictureUrl();
        this.positionLevel = user.getPositionLevel();
    }
}
