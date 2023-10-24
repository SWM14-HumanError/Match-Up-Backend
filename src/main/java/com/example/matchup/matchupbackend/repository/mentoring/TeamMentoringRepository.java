package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.entity.ApplyStatus;
import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.TeamMentoring;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMentoringRepository extends JpaRepository<TeamMentoring, Long> {

    List<TeamMentoring> findAllByTeamAndStatusAndMentoringOrderByEndedDateDesc(Team team, ApplyStatus applyStatus, Mentoring mentoring);

    List<TeamMentoring> findAllByTeam(Team team);
}
