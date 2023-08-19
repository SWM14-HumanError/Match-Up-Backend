package com.example.matchup.matchupbackend.repository.team;

import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.TeamTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
    @Query("SELECT tag from TeamTag tag where tag.team.id =:teamID")
    List<TeamTag> findTeamTagByTeamId(@Param("teamID") Long teamID);

    Team findTeamById(Long Id);
}
