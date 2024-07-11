package com.example.matchup.matchupbackend.repository.feedback;

import com.example.matchup.matchupbackend.dto.request.teamuser.FeedbackGrade;
import com.example.matchup.matchupbackend.entity.Feedback;
import com.example.matchup.matchupbackend.entity.QFeedback;
import com.example.matchup.matchupbackend.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.matchup.matchupbackend.entity.QFeedback.*;
import static com.example.matchup.matchupbackend.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class FeedbackCustomRepositoryImpl implements FeedbackCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final QFeedback qFeedback = feedback;
    private final QUser qUser = user;

    @Override
    public List<Feedback> findUserFeedbackByGrade(Long userID, String grade) {
        return queryFactory.selectFrom(feedback)
                .where(feedback.receiver.id.eq(userID), feedback.receiver.feedbackHider.eq(false),
                        gradeEq(grade))
                .join(feedback.receiver, user).fetchJoin()
                .orderBy(feedback.createTime.desc())
                .fetch();
    }

    private BooleanExpression gradeEq(String grade) {
        return (grade != null) ? feedback.grade.eq(FeedbackGrade.valueOf(grade)) : null;
    }
}
