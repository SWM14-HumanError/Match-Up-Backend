package com.example.matchup.matchupbackend.repository.team;

import com.example.matchup.matchupbackend.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
}
