package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Likes extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_receiver_id")
    private User likeReceiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentoring_id")
    private Mentoring mentoring;

    /**
     * 피드 좋아요
     * 혹은 프로젝트, 스터디 좋아요
     */
    @Builder
    private Likes(User user, User likeReceiver, Feed feed, Team team, Mentoring mentoring) {
        this.user = user;
        this.likeReceiver = likeReceiver;
        this.feed = feed;
        this.team = team;
        this.mentoring = mentoring;
    }
}
