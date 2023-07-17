package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "team")
public class Team {

    @Id
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_title")
    private String Title;

    @Column(name = "team_content")
    private String Content;

    @Column(name = "type")
    private String type; //스터디인지 프로젝트 모임인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String recruitFinish;
}