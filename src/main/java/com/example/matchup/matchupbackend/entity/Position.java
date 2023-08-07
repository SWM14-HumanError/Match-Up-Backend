package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "position")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long id;
    @Column(name = "role")
    private String role;
    @Column(name = "max_count")
    private Long maxCount;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    private List<TeamTag> tags = new ArrayList<>();

    @Builder
    public Position(String role, Long maxCount) {
        this.role = role;
        this.maxCount = maxCount;
    }

    //== 연관관계 로직 ==//
    public Long addTeam(Team team) {
        this.team = team;
        team.addPosition(this);
        return team.getId();
    }
}
