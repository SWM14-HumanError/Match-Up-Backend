package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("select feedback from Feedback feedback where feedback.team.id=:teamID and feedback.giver.id=:giverID and feedback.receiver.id=:receiverID order by feedback.createTime desc")
    List<Feedback> findFeedbackByUserAndTeam(Long giverID, Long receiverID, Long teamID);
}
