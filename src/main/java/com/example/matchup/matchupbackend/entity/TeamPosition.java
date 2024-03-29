package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.Member;
import com.example.matchup.matchupbackend.global.RoleType;
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
@Table(name = "team_position")
public class TeamPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "count")
    private Long count;
    @Column(name = "max_count")
    private Long maxCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @OneToMany(mappedBy = "teamPosition", cascade = CascadeType.ALL)
    private List<TeamTag> tags = new ArrayList<>();

    @Builder
    public TeamPosition(RoleType role, Long count, Long maxCount, Team team, List<TeamTag> tags) {
        this.role = role;
        this.count = count;
        this.maxCount = maxCount;
        this.team = team;
        this.tags = tags;
    }

    //== 비즈니스 로직 ==//
    public List<String> stringTagList() {
        List<String> tagList = new ArrayList<>();
        this.tags.stream().forEach(tag -> {
            tagList.add(tag.getTag().getName());
        });
        return tagList;
    }

    public TeamPosition updateTeamPosition(Member members)
    {
        this.maxCount = members.getMaxCount();
        return this;
    }

    public static TeamPosition of(RoleType role, Long count, Long maxCount, Team team) {
        TeamPosition build = TeamPosition.builder()
                .role(role)
                .count(count)
                .maxCount(maxCount)
                .team(team)
                .build();
        return build;
    }
}
