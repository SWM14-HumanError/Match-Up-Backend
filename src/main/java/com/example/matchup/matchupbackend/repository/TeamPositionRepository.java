package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.TeamPosition;
import com.example.matchup.matchupbackend.global.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamPositionRepository extends JpaRepository<TeamPosition, Long> {
    @Query("SELECT position from TeamPosition position where position.team.id=:teamID")
    List<TeamPosition> findTeamPositionListByTeamId(@Param("teamID") Long teamID);

    @Query("SELECT position from TeamPosition position where position.team.id=:teamID and position.role=:role")
    Optional<TeamPosition> findTeamPositionByTeamIdAndRole(@Param("teamID") Long teamID, @Param("role") RoleType role);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TeamPosition position SET position.count = position.count + 1 WHERE position.team.id = :teamId AND position.role=:role")
    void updateTeamPositionStatusByAcceptUser(@Param("teamId") Long teamId, @Param("role") RoleType role);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TeamPosition position SET position.count = position.count - 1 WHERE position.team.id = :teamId AND position.role=:role")
    void updateTeamPositionStatusByKickedUser(@Param("teamId") Long teamId, @Param("role") RoleType role);

    @Query("SELECT position from TeamPosition position join fetch position.team where position.team.id=:teamId")
    List<TeamPosition> findPositionJoinTeamByTeamId(@Param("teamId") Long teamId);

    List<TeamPosition> findByTeam(Team team);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM TeamPosition tp WHERE tp.team.id = :teamId AND tp.count = 0")
    void deleteNoTeamUserByTeamId(@Param("teamId") Long teamId);
}
