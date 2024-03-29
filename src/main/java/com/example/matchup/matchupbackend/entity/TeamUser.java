package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.teamuser.RecruitFormRequest;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_user")
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_user_id")
    private Long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "count")
    private Long count;
    @Column(name = "approve", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean approve = false;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "recruit_id", nullable = true)
    private TeamRecruit teamRecruit;

    @OneToMany(mappedBy = "teamUser", cascade = CascadeType.PERSIST)
    private List<Feedback> feedback;

    @Builder
    public TeamUser(RoleType role, Long count, Boolean approve, Team team, User user, TeamRecruit teamRecruit) {
        this.role = role;
        this.count = count;
        this.approve = approve;
        this.team = team;
        this.user = user;
        this.teamRecruit = teamRecruit;
    }

    //== 비즈니스 로직 ==//
    public void approveUser() {
        this.approve = true;
    }

    public static TeamUser of(RecruitFormRequest recruitForm, TeamPosition teamPosition, Team team, User user, TeamRecruit teamRecruit)
    {
        TeamUser build = TeamUser.builder()
                .role(recruitForm.getRole())
                .approve(false)
                .count(teamPosition.getCount())
                .team(team)
                .user(user)
                .teamRecruit(teamRecruit)
                .build();
        return build;
    }

    public static TeamUser of(RoleType role, Long count, Boolean approve, Team team, User user) {
        TeamUser build = TeamUser.builder()
                .count(count)
                .user(user)
                .team(team)
                .role(role)
                .approve(approve)
                .build();
        return build;
    }
}