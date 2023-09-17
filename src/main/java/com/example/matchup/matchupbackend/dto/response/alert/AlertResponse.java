package com.example.matchup.matchupbackend.dto.response.alert;

import com.example.matchup.matchupbackend.entity.AlertType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertResponse {
    private Long id;
    private String title;
    private LocalDateTime createdDate;
    private String content;
    private boolean isRead;
    private String redirectUrl;
    private AlertType alertType;
}
