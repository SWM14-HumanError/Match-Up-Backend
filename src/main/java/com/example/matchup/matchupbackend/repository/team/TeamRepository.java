package com.example.matchup.matchupbackend.repository.team;

import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.TeamTag;
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
    boolean isFinished(Long teamID);
}
