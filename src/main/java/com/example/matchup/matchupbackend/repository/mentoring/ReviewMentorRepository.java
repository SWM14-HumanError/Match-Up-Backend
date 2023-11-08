package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.ReviewMentor;
import com.example.matchup.matchupbackend.entity.TeamMentoring;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewMentorRepository extends JpaRepository<ReviewMentor, Long> {

    boolean existsByTeamMentoringAndMentee(TeamMentoring latestEndedTeamMentoring, User mentee);

    int countByMentoring(Mentoring mentoring);
}
