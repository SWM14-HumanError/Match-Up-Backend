package com.example.matchup.matchupbackend.repository.team;

import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.TeamTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
    @Query("SELECT tag from TeamTag tag where tag.team.id =:teamID")
    List<TeamTag> findTeamTagByTeamId(@Param("teamID") Long teamID);

    Optional<Team> findTeamById(Long Id);
    @Query("select " +
            "case " +
            "when team.isDeleted = 0L then false " +
            "when team.isDeleted = 1L then true end " +
            "from Team team where team.id=:teamID")
    boolean isFinished(@Param(value = "teamID") Long teamID);

    @Query("select team from Team team join fetch team.teamUserList where team.id=:id")
    Optional<Team> findTeamJoinTeamUserById(@Param("id") Long Id);

    Slice<Team> findAllByIdIn(List<Long> teamId, Pageable pageable);

    List<Team> findByLeaderIDAndIsDeletedAndType(Long userId, Long isDeleted, Long type);

    Optional<Team> findTeamByIdAndIsDeleted(Long teamId, Long isDeleted);

    @Query("select team from Team team where team.leaderID = :userId and team.isDeleted=0L and team.isFinished=0L")
    List<Team> findActiveTeamByLeaderID(@Param("userId") Long userId);

}
