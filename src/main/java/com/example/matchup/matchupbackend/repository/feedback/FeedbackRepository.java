package com.example.matchup.matchupbackend.repository.feedback;

import com.example.matchup.matchupbackend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, FeedbackCustomRepository{
    @Query("select feedback from Feedback feedback where feedback.team.id=:teamID and feedback.giver.id=:giverID and feedback.receiver.id=:receiverID order by feedback.createTime desc")
    List<Feedback> findFeedbackByUserAndTeam(@Param(value = "giverID") Long giverID, @Param(value = "receiverID") Long receiverID, @Param(value = "teamID") Long teamID);

    @Query("select feedback from Feedback feedback " +
            "join fetch feedback.receiver " +
            "where feedback.team.id=:teamId and feedback.giver.id=:giverId")
    List<Feedback> findFeedbacksJoinReceiverBy(@Param("giverID") Long giverId, @Param("teamId") Long teamId);
}
