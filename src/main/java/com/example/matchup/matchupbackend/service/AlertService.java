package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.alert.AlertFilterRequest;
import com.example.matchup.matchupbackend.dto.response.alert.AlertResponse;
import com.example.matchup.matchupbackend.dto.response.alert.SliceAlertResponse;
import com.example.matchup.matchupbackend.entity.Alert;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.AlertDeletedException;
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
        log.info("시간대: {}", alertResponseList.get(0).getCreatedDate());
        return new SliceAlertResponse(alertResponseList, alertSlice.getSize(), alertSlice.hasNext());
    }

    @Transactional
    public void setAlertStatusRead(Long alertId, Long userId) {
        Alert alert = getAlertWithValid(alertId, userId);
        alert.readAlert();
    }

    @Transactional
    public void deleteAlert(Long alertId, Long userId) {
        Alert alert = getAlertWithValid(alertId, userId);
        alert.deleteAlert();
    }

    @Transactional // 트렌젝션 안붙여도 이전 메서드에서 트렌젝션 전파 되나?
    public Alert getAlertWithValid(Long alertId, Long userId) {
        Alert alert = alertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new AlertNotFoundException("alertID: " + alertId));
        if (alert.isDeleted() == true) // 알림이 이미 삭제 된 경우
            throw new AlertDeletedException("alertID: " + alertId);
        return alert;
    }
}
