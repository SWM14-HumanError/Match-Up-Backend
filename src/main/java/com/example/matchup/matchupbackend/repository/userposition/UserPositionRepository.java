package com.example.matchup.matchupbackend.repository.userposition;

import com.example.matchup.matchupbackend.entity.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
}
