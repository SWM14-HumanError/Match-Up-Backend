package com.example.matchup.matchupbackend.repository.alert;

import com.example.matchup.matchupbackend.dto.request.alert.AlertFilterRequest;
import com.example.matchup.matchupbackend.entity.Alert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AlertRepositoryCustom {
    Slice<Alert> findAlertSliceByAlertRequest(Long userId,AlertFilterRequest alertRequest, Pageable pageable);
}
