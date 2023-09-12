package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
