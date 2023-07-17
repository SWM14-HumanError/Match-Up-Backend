package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "review")
public class Review {

    @Id
    @Column(name = "review_id")
    private Long id;

    @Column(name = "review_score")
    private Long score;

    @Column(name = "review_title")
    private String title;

    @Column(name = "review_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentoring_id")
    private Mentoring mentoring;
}