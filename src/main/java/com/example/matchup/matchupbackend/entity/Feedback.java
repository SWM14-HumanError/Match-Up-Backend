package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.teamuser.FeedbackGrade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feedback")
public class Feedback {
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
    private Integer totalScore;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "giver_id")
    private User giver;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_id")
    private User receiver;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    //== 비즈니스 로직 ==//
    public Integer calculateTotalScore() {
        Integer totalScore = 0;
        if (contactable) totalScore += 1;
        if (onTime) totalScore += 1;
        if (responsible) totalScore += 1;
        if (kind) totalScore += 1;
        if (collaboration) totalScore += 1;
        if (fast) totalScore += 1;
        if (actively) totalScore += 1;
        return totalScore;
    }
}
/**
 * teamuser를 안넣는 이유는 리뷰는 팀원으로써 보다 한 유저의 리뷰이기 때문에
 */