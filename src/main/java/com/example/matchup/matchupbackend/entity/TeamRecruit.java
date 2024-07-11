package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.teamuser.RecruitFormRequest;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_recruit")
public class TeamRecruit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id")
    private Long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "content")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne(mappedBy = "teamRecruit", fetch = FetchType.LAZY)
    private TeamUser teamUser;

    @Builder
    public TeamRecruit(RoleType role, String content, Team team, User user) {
        this.role = role;
        this.content = content;
        this.team = team;
        this.user = user;
    }

    public static TeamRecruit of(RecruitFormRequest recruitForm, User user, Team team)
    {
        TeamRecruit teamRecruit = TeamRecruit.builder()
                .role(recruitForm.getRole())
                .content(recruitForm.getContent())
                .team(team)
                .user(user)
                .build();
        return teamRecruit;
    }
}
