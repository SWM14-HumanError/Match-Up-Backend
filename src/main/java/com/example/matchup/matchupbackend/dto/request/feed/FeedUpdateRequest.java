package com.example.matchup.matchupbackend.dto.request.feed;

import com.example.matchup.matchupbackend.entity.ProjectDomain;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FeedUpdateRequest {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long type;
    private ProjectDomain domain;
}
