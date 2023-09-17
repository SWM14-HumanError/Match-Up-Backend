package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.alert.AlertFilterRequest;
import com.example.matchup.matchupbackend.dto.response.alert.AlertResponse;
import com.example.matchup.matchupbackend.dto.response.alert.SliceAlertResponse;
import com.example.matchup.matchupbackend.entity.Alert;
import com.example.matchup.matchupbackend.repository.alert.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;

    public SliceAlertResponse getSliceAlertResponse(Long userId, AlertFilterRequest alertFilterRequest, Pageable pageable) {
        return null;
    }

    public AlertResponse getAlertResponse(Long userId, AlertFilterRequest alertFilterRequest, Pageable pageable) {
        Slice<Alert> alertSlice = alertRepository.findAlertSliceByAlertRequest(userId,alertFilterRequest, pageable);
    }
}
