package com.example.matchup.matchupbackend.dto.request.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import com.example.matchup.matchupbackend.entity.User;
import lombok.Data;

@Data
public class FeedCreateRequest {

    private String title;
    private String content;
    private String imageUrl;
    private Long type;
    private ProjectDomain domain;

    public Feed toEntity(User user) {
        return Feed.builder()
                .title(title)
                .content(content)
                .thumbnailUrl(imageUrl)
                .type(type)
                .projectDomain(domain)
                .user(user)
                .build();
    }
}
