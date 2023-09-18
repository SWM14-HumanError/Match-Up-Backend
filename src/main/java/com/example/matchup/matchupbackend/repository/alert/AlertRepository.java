package com.example.matchup.matchupbackend.repository.alert;

import com.example.matchup.matchupbackend.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long>, AlertRepositoryCustom {
    Optional<Alert> findByIdAndUserId(Long alertId, Long userId);
}
