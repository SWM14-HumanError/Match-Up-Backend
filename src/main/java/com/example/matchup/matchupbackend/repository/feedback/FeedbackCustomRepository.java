package com.example.matchup.matchupbackend.repository.feedback;

import com.example.matchup.matchupbackend.entity.Feedback;

import java.util.List;

public interface FeedbackCustomRepository {
    List<Feedback> findUserFeedbackJoinUser(Long userID, String grade);
}
