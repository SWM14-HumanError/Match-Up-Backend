package com.example.matchup.matchupbackend.repository.teamuser;

import com.example.matchup.matchupbackend.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
}
