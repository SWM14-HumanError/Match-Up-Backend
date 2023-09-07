package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Feed extends BaseTimeEntity{
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
    private Long likeCount; // 아직 미구현(디자인 상에 구현되어 있지 않음)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Feed(String title, String content, Long type, ProjectDomain projectDomain, String thumbnailUrl, Long likeCount, User user) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.projectDomain = projectDomain;
        this.thumbnailUrl = thumbnailUrl;
        this.likeCount = likeCount;
        this.user = user;
    }
}
