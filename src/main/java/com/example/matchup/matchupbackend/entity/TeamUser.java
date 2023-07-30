package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.TeamUserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
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
    @Column(name = "count")
    private Long count;
    @Column(name = "approve")
    private Boolean approve; //0은 미승인 1은 승인됨

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public TeamUser(String role, Long count, Team team, User user) {
        this.role = role;
        this.count = count;
        this.team = team;
        this.user = user;
    }

    //== 비즈니스 로직 ==//

}