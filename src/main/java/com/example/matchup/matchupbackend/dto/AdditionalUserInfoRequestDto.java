package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.entity.MeetingType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AdditionalUserInfoRequestDto {
    private Long positionLevel;
    private LocalDate userBirthday;
    private Long userLevel;
    private String address;
    private Long expYear;
    private String expertize;
    private MeetingType meetingType;
    private String position;
}
