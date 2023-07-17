package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
@Entity
@Getter
public class Feed {
    @Id
    @Column(name = "feed_id")
    private Long id;

    @Column(name = "feed_title")
    private String title;

    @Column(name = "feed_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
