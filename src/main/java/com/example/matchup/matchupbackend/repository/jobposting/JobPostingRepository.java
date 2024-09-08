package com.example.matchup.matchupbackend.repository.jobposting;

import com.example.matchup.matchupbackend.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long>, JobPostingCustom {
}
