package com.example.matchup.matchupbackend.repository.alert;

import com.example.matchup.matchupbackend.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long>, AlertRepositoryCustom {
}
