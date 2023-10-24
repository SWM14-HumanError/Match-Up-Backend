package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.ReviewMentor;
import com.example.matchup.matchupbackend.entity.TeamMentoring;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewMentorRepository extends JpaRepository<ReviewMentor, Long> {
    boolean existsByTeamMentoring(TeamMentoring latestEndedTeamMentoring);

    int countByMentoring(Mentoring mentoring);
}
