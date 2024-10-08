package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.feed.FeedCommentCreateOrUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "comment_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Comment(String content, Feed feed, User user) {
        this.content = content;
        this.feed = feed;
        this.user = user;
    }

    public Comment updateFeedComment(FeedCommentCreateOrUpdateRequest request) {
        this.content = request.getContent();
        return this;
    }
}