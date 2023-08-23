package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.TeamPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamPositionRepository extends JpaRepository<TeamPosition, Long> {
    @Query("SELECT position from TeamPosition position where position.team.id=:teamID")
    List<TeamPosition> findTeamPositionListByTeamId(@Param("teamID") Long teamID);

    @Query("SELECT position from TeamPosition position where position.team.id=:teamID and position.role=:role")
    TeamPosition findTeamPositionByTeamIdAndRole(@Param("teamID") Long teamID, @Param("role") String role);

    @Modifying
    @Query("UPDATE TeamPosition position SET position.count = position.count + 1 WHERE position.team.id = :teamId AND position.role=:role")
    void updateTeamPositionStatusByAcceptUser(@Param("teamId") Long teamId, @Param("role") String role);

    @Modifying
    @Query("UPDATE TeamPosition position SET position.count = position.count - 1 WHERE position.team.id = :teamId AND position.role=:role")
    void updateTeamPositionStatusByKickedUser(@Param("teamId") Long teamId, @Param("role") String role);
}
