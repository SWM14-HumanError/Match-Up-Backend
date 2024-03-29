package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.teamuser.FeedbackGrade;
import com.example.matchup.matchupbackend.dto.request.teamuser.TeamUserFeedbackRequest;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidFeedbackException;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidFeedbackGradeException;
import com.example.matchup.matchupbackend.global.Values;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feedback")
public class Feedback extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private FeedbackGrade grade;
    @Column(name = "contactable")
    private Boolean contactable; // 연락 잘되는지
    @Column(name = "on_time")
    private Boolean onTime; // 시간 잘지키는지
    @Column(name = "responsible")
    private Boolean responsible; // 책임감 있는지
    @Column(name = "kind")
    private Boolean kind; // 친절한지
    @Column(name = "collaboration")
    private Boolean collaboration; // 협업하기 좋은 사람인지
    @Column(name = "fast")
    private Boolean fast; // 개발 속도가 빠른지
    @Column(name = "actively")
    private Boolean actively;
    @Column(name = "comment_to_user")
    private String commentToUser;
    @Column(name = "comment_to_admin")
    private String commentToAdmin;
    @Column(name = "total_score")
    private Double totalScore;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "giver_id")
    private User giver;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "receiver_id")
    private User receiver;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id")
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_user_id")
    private TeamUser teamUser;

    //== 연관관계 메서드==//
    @Builder
    public Feedback(Long id, FeedbackGrade grade, Boolean contactable, Boolean onTime, Boolean responsible, Boolean kind, Boolean collaboration, Boolean fast, Boolean actively, String commentToUser, String commentToAdmin, Double totalScore, User giver, User receiver, Team team, TeamUser teamUser) {
        this.id = id;
        this.grade = grade;
        this.contactable = contactable;
        this.onTime = onTime;
        this.responsible = responsible;
        this.kind = kind;
        this.collaboration = collaboration;
        this.fast = fast;
        this.actively = actively;
        this.commentToUser = commentToUser;
        this.commentToAdmin = commentToAdmin;
        this.totalScore = totalScore;
        this.giver = giver;
        this.receiver = receiver;
        this.team = team;
        this.teamUser = teamUser;
    }

    private void setGiver(User giver) {
        this.giver = giver;
        giver.getGiveFeedbackList().add(this);
    }

    private void setReceiver(User receiver) {
        this.receiver = receiver;
        receiver.getRecieveFeedbackList().add(this);
    }

    private void setTeam(Team team) {
        this.team = team;
        team.getTeamUserFeedbackList().add(this);
    }

    private void setTeamUser(TeamUser teamUser) {
        if(teamUser == null) throw new InvalidFeedbackException("teamUser가 null입니다.");
        this.teamUser = teamUser;
        teamUser.getFeedback().add(this);
    }

    public void setRelation(User giver, User receiver, Team team, TeamUser teamUser) {
        setGiver(giver);
        setReceiver(receiver);
        setTeam(team);
        setTeamUser(teamUser);
    }

    //== 비즈니스 로직 ==//
    public LocalDateTime timeToFeedback() {
        return this.getUpdateTime().plusDays(Values.FEEDBACK_PERIOD.getValue());
    }

    /**
     * Feedback에 따른 점수를 더해줌
     */
    private Double calculateTotalScore(FeedbackGrade grade) {
        Double totalScore = 0.0;
        Double addScore = addScoreByGrade(grade);
        if (contactable) totalScore += addScore;
        if (onTime) totalScore += addScore;
        if (responsible) totalScore += addScore;
        if (kind) totalScore += addScore;
        if (collaboration) totalScore += addScore;
        if (fast) totalScore += addScore;
        if (actively) totalScore += addScore;
        if (totalScore == 0.0) {
            throw new InvalidFeedbackException(totalScore.toString(), "최소 1개 이상의 피드백을 선택해주세요");
        }
        return totalScore;
    }

    /**
     * FeedbackGrade에 따른 더할 점수를 반환
     * @param grade
     * @return
     */
    public Double addScoreByGrade(FeedbackGrade grade) {
        if (grade.equals(FeedbackGrade.GREAT)) return 0.2;
        else if (grade.equals(FeedbackGrade.NORMAL)) return 0.1;
        else if (grade.equals(FeedbackGrade.BAD)) return -0.1;
        else throw new InvalidFeedbackGradeException(grade.toString());
    }

    public static Feedback fromDTO(TeamUserFeedbackRequest feedback) {
        Feedback build = Feedback.builder()
                .grade(feedback.getGrade())
                .contactable(feedback.getIsContactable())
                .onTime(feedback.getIsOnTime())
                .responsible(feedback.getIsResponsible())
                .kind(feedback.getIsKind())
                .collaboration(feedback.getIsCollaboration())
                .fast(feedback.getIsFast())
                .actively(feedback.getIsActively())
                .commentToUser(feedback.getCommentToUser())
                .commentToAdmin(feedback.getCommentToAdmin())
                .build();
        build.totalScore = build.calculateTotalScore(feedback.getGrade());
        return build;
    }
}