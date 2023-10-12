package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.InviteTeam;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteTeamRepository extends JpaRepository<InviteTeam, Long> {
    boolean existsByReceiverAndSenderAndTeam(User receiver, User sender, Team team);
}
