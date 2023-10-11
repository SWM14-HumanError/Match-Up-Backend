package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.teamuser.RefuseFormRequest;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_refuse")
public class TeamRefuse extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_refuse_id")
    private Long id;

    @Column(name = "refuse_reason")
    private String refuseReason;

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private RoleType position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User refusedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    public TeamRefuse(String refuseReason, RoleType position, User refusedUser, Team team) {
        this.refuseReason = refuseReason;
        this.position = position;
        this.refusedUser = refusedUser;
        this.team = team;
    }

    public static TeamRefuse of(RefuseFormRequest refuseForm, TeamUser teamUser) {
        return TeamRefuse.builder()
                .refuseReason(refuseForm.getRefuseReason())
                .position(teamUser.getRole())
                .refusedUser(teamUser.getUser())
                .team(teamUser.getTeam())
                .build();
    }

    //==연관관계 메서드==//
    public void setTeam(Team team) {
        this.team = team;
        team.getTeamRefuseList().add(this);
    }

    public void setUser(User user) {
        this.refusedUser = user;
        user.getTeamRefuses().add(this);
    }

}
