package com.example.matchup.matchupbackend.dto.response.alert;

import com.example.matchup.matchupbackend.entity.Alert;
import com.example.matchup.matchupbackend.entity.AlertType;
import lombok.Builder;
import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class AlertResponse {
    private Long id;
    private String title;
    private ZonedDateTime createdDate;
    private String content;
    private boolean isRead;
    private String redirectUrl;
    private AlertType alertType;

    public static AlertResponse from(Alert alert) {
        AlertResponse alertResponse = AlertResponse.builder()
                .id(alert.getId())
                .title(alert.getTitle())
                .createdDate(ZonedDateTime.of(alert.getCreateTime(), ZoneId.of("Asia/Seoul")))
                .content(alert.getContent())
                .isRead(alert.isRead())
                .redirectUrl(alert.getRedirectUrl())
                .alertType(alert.getAlertType())
                .build();
        return alertResponse;
    }

    @Builder
    public AlertResponse(Long id, String title, ZonedDateTime createdDate, String content, boolean isRead, String redirectUrl, AlertType alertType) {
        this.id = id;
        this.title = title;
        this.createdDate = createdDate;
        this.content = content;
        this.isRead = isRead;
        this.redirectUrl = redirectUrl;
        this.alertType = alertType;
    }
}
