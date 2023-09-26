package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.TeamRefuse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TeamRefuseRepository extends JpaRepository<TeamRefuse, Long> {
    @Query("select teamrefuse from TeamRefuse teamrefuse " +
            "join fetch teamrefuse.refusedUser " +
            "join fetch teamrefuse.team " +
            "WHERE teamrefuse.id=:refuseId")
    Optional<TeamRefuse> findRefuseInfoJoinUserAndTeamById(Long refuseId);
}
