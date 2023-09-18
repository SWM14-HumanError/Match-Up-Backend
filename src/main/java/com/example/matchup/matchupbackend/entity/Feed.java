package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.feed.FeedCreateOrUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Feed extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    @Column(name = "feed_title")
    private String title;

    @Column(name = "feed_content")
    private String content;
    private Long type; // 0 -> project, 1 ->  study

    @Enumerated(EnumType.STRING)
    private ProjectDomain projectDomain;
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "feed")
    private List<Likes> likes = new ArrayList<>();

    @Builder
    public Feed(String title, String content, Long type, ProjectDomain projectDomain, String thumbnailUrl, User user) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.projectDomain = projectDomain;
        this.thumbnailUrl = thumbnailUrl;
        this.user = user;
    }

    //- 비즈니스 로직 -//
    public Feed updateFeed(FeedCreateOrUpdateRequest request) {
        this.title = (request.getTitle() == null) ? this.title : request.getTitle();
        this.content = (request.getContent() == null) ? this.content : request.getContent();
        this.type = request.getType();
        this.projectDomain = (request.getDomain() == null) ? this.projectDomain : request.getDomain();
        this.thumbnailUrl = (request.getImageUrl() == null) ? this.thumbnailUrl : request.getImageUrl();
        return this;
    }
}
