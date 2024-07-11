package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.TeamRecruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRecruitRepository extends JpaRepository<TeamRecruit, Long> {
    @Query("select teamrecruit from TeamRecruit teamrecruit join fetch teamrecruit.user where teamrecruit.id = :recruitId")
    Optional<TeamRecruit> findRecruitJoinUserById(@Param("recruitId") Long recruitId);
}
