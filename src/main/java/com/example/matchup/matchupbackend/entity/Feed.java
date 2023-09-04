package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
@Entity
@Getter
public class Feed extends BaseTimeEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    @Column(name = "feed_title")
    private String title;

    @Column(name = "feed_content")
    private String content;

    private String thumbnailUrl;
    private Long likeCount; // 아직 미구현(디자인 상에 구현되어 있지 않음)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
