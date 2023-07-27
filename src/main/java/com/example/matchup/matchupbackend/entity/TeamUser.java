package com.example.matchup.matchupbackend.entity;

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
    private String role;
    @Column(name = "stacks")
    private String stacks; //어떻게 할지 결정(stack 테이블 만들어야하는지)
    @Column(name = "count")
    private Long count;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public TeamUser(String role, String stacks, Long count, Team team, User user) {
        this.role = role;
        this.stacks = stacks;
        this.count = count;
        this.team = team;
        this.user = user;
    }
}