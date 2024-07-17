package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.InviteTeam;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InviteTeamRepository extends JpaRepository<InviteTeam, Long> {
    boolean existsByReceiverAndSenderAndTeam(User receiver, User sender, Team team);

    @Query("select inviteteam from InviteTeam inviteteam " +
            "join fetch inviteteam.team " +
            "where inviteteam.sender=:sender and inviteteam.receiver=:receiver")
    List<InviteTeam> findAllBySenderAndReceiver(@Param("sender") User sender, @Param("receiver") User receiver);
}
