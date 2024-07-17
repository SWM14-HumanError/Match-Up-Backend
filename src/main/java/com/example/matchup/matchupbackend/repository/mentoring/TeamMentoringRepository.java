package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMentoringRepository extends JpaRepository<TeamMentoring, Long> {

    List<TeamMentoring> findAllByTeamAndStatusAndMentoringOrderByEndedDateDesc(Team team, ApplyStatus applyStatus, Mentoring mentoring);

    Optional<TeamMentoring> findTopByTeamAndStatusAndMentoringOrderByIdDesc(Team team, ApplyStatus applyStatus, Mentoring mentoring);

    List<TeamMentoring> findALlByMentoringInAndStatus(List<Mentoring> mentorings, ApplyStatus status);

    @Query("SELECT distinct tm from TeamMentoring tm join fetch tm.mentoring where tm.team=:team and tm.status in :acceptedOrEnded")
    List<TeamMentoring> findAllDistinctByTeamAndTeamMentoringStatusIn(@Param("team") Team team, @Param("acceptedOrEnded") List<ApplyStatus> acceptedOrEnded);

    @Query("SELECT tm from TeamMentoring tm " +
            "join fetch tm.mentoring " +
            "join fetch tm.team " +
            "where tm.mentoring.mentor= :mentor and tm.status = com.example.matchup.matchupbackend.entity.ApplyStatus.WAITING and tm.mentoring.isDeleted=false " +
            "order by tm.mentoring.id desc")
    List<TeamMentoring> findWaitMentoringJoinTeamAndMentoringByMentor(@Param("mentor") User mentor);

    @Query("select tm from TeamMentoring tm " +
            "join fetch tm.team " +
            "join fetch tm.mentoring " +
            "where tm.id=:id")
    Optional<TeamMentoring> findTeamMentoringJoinTeamAndMentoringById(@Param("id") Long id);
}
