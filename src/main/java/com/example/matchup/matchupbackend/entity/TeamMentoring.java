package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "mentoring_team")
public class TeamMentoring {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentoring_team_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentoring_id")
    private Mentoring mentoring;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
