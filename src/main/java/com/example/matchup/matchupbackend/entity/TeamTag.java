package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_tag")
public class TeamTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_id")
    private Tag tag;
    @Builder
    public TeamTag(Team team, Tag tag) {
        this.team = team;
        this.tag = tag;
    }
}