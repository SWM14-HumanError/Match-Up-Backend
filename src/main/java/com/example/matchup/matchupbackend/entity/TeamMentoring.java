package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyMentoringRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.example.matchup.matchupbackend.entity.ApplyStatus.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeamMentoring extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taem_mentoring_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplyStatus status;

    private String phoneNumber;

    private String emailOfTeamLeader;

    private String messageFromLeader;

    private LocalDate endedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentoring_id", nullable = false)
    private Mentoring mentoring;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Builder
    private TeamMentoring(ApplyStatus status, String phoneNumber, String emailOfTeamLeader, String messageFromLeader, Mentoring mentoring, Team team) {
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.emailOfTeamLeader = emailOfTeamLeader;
        this.messageFromLeader = messageFromLeader;
        this.mentoring = mentoring;
        this.team = team;
    }

    public static TeamMentoring create(Mentoring mentoring, Team team, ApplyMentoringRequest request) {
        return TeamMentoring.builder()
                .status(WAITING)
                .mentoring(mentoring)
                .team(team)
                .phoneNumber(request.getPhoneNumber())
                .emailOfTeamLeader(request.getEmail())
                .messageFromLeader(request.getContent())
                .build();
    }

    public void acceptMentoring() {
        this.status = ACCEPTED;
    }

    public void refuseMentoring() {
        this.status = REFUSED;
    }

    public void endMentoring() {
        this.status = ENDED;
    }

}
