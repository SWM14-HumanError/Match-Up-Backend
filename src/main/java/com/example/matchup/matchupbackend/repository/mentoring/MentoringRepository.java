package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MentoringRepository extends JpaRepository<Mentoring, Long>, MentoringRepositoryCustom {

    Optional<Mentoring> findByIdAndIsDeleted(Long mentoringId, boolean isDeleted);

    List<Mentoring> findALlByMentorOrderByIdDesc(User mentor);
}
