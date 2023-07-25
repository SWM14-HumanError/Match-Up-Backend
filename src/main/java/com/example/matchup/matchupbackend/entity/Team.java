package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "team")
public class Team extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_title")
    private String title;

    @Column(name = "team_content")
    private String content;

    @Column(name = "type")
    private Long type; //스터디인지 프로젝트 모임인지

    @Column(name = "thumbnailUrl")
    private String thumbnailUrl;

    @Column(name = "content_like")
    private Long like;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "team")
    private List<Tag> tags;

    private String recruitFinish;
}