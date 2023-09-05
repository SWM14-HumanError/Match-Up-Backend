package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.teamuser.RecruitFormRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String role;
    @Column(name = "count")
    private Long count;
    @Column(name = "approve")
    private Boolean approve;
    @Column(name = "max_count")
    private Long maxCount;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public TeamUser(String role, Long count, Boolean approve, Long maxCount, Team team, User user) {
        this.role = role;
        this.count = count;
        this.approve = approve;
        this.maxCount = maxCount;
        this.team = team;
        this.user = user;
    }

    //== 비즈니스 로직 ==//
    public void approveUser() {
        this.approve = true;
    }

    public static TeamUser of(RecruitFormRequest recruitForm, TeamPosition teamPosition, Team team, User user)
    {
        TeamUser build = TeamUser.builder()
                .role(recruitForm.getRole())
                .approve(false)
                .count(teamPosition.getCount())
                .maxCount(teamPosition.getMaxCount())
                .team(team)
                .user(user)
                .build();
        return build;
    }

    public static TeamUser of(String role, Long count, Boolean approve, Long maxCount, Team team, User user) {
        TeamUser build = TeamUser.builder()
                .count(count)
                .maxCount(maxCount) //팀 리더는 한명으로 설정
                .user(user)
                .team(team)
                .role(role)
                .approve(approve)
                .build();
        return build;
    }
}