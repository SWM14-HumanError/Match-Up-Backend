package com.example.matchup.matchupbackend.repository.feedback;

import com.example.matchup.matchupbackend.entity.Feedback;

import java.util.List;

public interface FeedbackCustomRepository {
    List<Feedback> findUserFeedbackByGrade(Long userID, String grade);
}
