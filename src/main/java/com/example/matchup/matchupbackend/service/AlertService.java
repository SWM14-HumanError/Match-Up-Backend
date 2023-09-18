package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.alert.AlertFilterRequest;
import com.example.matchup.matchupbackend.dto.response.alert.AlertResponse;
import com.example.matchup.matchupbackend.dto.response.alert.SliceAlertResponse;
import com.example.matchup.matchupbackend.entity.Alert;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.AlertNotFoundException;
import com.example.matchup.matchupbackend.repository.alert.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;

    public SliceAlertResponse getSliceAlertResponse(Long userId, AlertFilterRequest alertFilterRequest, Pageable pageable) {
        Slice<Alert> alertSlice = alertRepository.findAlertSliceByAlertRequest(userId, alertFilterRequest, pageable);
        List<AlertResponse> alertResponseList = alertSlice.getContent().stream()
                .map(alert -> AlertResponse.from(alert))
                .collect(Collectors.toList());
        return new SliceAlertResponse(alertResponseList, alertSlice.getSize(), alertSlice.hasNext());
    }

    @Transactional
    public void setAlertStatusRead(Long alertId, Long userId) {
        Alert alert = alertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new AlertNotFoundException("alertID: " + alertId));
        alert.readAlert();
    }
}
