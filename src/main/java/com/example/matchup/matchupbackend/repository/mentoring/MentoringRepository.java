package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.entity.ApplyStatus;
import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentoringRepository extends JpaRepository<Mentoring, Long>, MentoringRepositoryCustom {

    Optional<Mentoring> findByIdAndIsDeleted(Long mentoringId, boolean isDeleted);

    List<Mentoring> findALlByMentorAndIsDeletedOrderByIdDesc(User mentor, Boolean isDeleted);

    List<Mentoring> findAllByMentorAndIsDeleted(User mentor, boolean isDeleted);

    List<Mentoring> findAllByMentor(User mentor);

    @Query("SELECT distinct tm.mentoring from TeamMentoring tm join fetch tm.mentoring where tm.team=:team and tm.status in :acceptedOrEnded")
    List<Mentoring> findAllDistinctByTeamAndTeamMentoringStatusIn(@Param("team") Team team, @Param("acceptedOrEnded") List<ApplyStatus> acceptedOrEnded);
}
