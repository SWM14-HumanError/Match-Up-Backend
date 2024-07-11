package com.example.matchup.matchupbackend.repository.teamtag;

import com.example.matchup.matchupbackend.entity.TeamTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamTagRepository extends JpaRepository<TeamTag, Long> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM TeamTag tag WHERE tag.team.id = :teamId")
    void deleteByTeamId(@Param("teamId") Long teamId);
}
