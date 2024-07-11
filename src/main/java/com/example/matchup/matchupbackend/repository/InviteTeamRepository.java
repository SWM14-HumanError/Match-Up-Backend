package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.InviteTeam;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InviteTeamRepository extends JpaRepository<InviteTeam, Long> {
    boolean existsByReceiverAndSenderAndTeam(User receiver, User sender, Team team);

    List<InviteTeam> findAllByReceiverAndSender(User receiver, User sender);
}
