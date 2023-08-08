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
    @Column(name = "tag_name")
    private String tagName;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "position_id")
    private TeamPosition teamPosition;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public TeamTag(String tagName, TeamPosition teamPosition, Team team, Tag tag) {
        this.tagName = tagName;
        this.teamPosition = teamPosition;
        this.team = team;
        this.tag = tag;
    }

    //== 비즈니스 로직 ==//
    public void addTagName(String tagName) {
        this.tagName = tagName;
    }
}
