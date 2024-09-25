package com.example.matchup.matchupbackend.dto.request.alert;

import com.example.matchup.matchupbackend.entity.AlertType;
import com.example.matchup.matchupbackend.error.annotation.Enum;
import lombok.Data;

@Data
public class AlertFilterRequest {
    @Enum(enumClass = AlertType.class, message = "알림 타입은 PROJECT, STUDY, FEED, MENTORING, ETC 입니다.")
    private AlertType alertType;
    private int page;
    private int size;
}
