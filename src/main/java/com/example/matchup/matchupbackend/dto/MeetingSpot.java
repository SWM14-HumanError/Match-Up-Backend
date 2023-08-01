package com.example.matchup.matchupbackend.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeetingSpot {
    private String onOffline;
    private String city;
    private String detailSpot;

    @QueryProjection
    public MeetingSpot(String onOffline, String city, String detailSpot) {
        this.onOffline = onOffline;
        this.city = city;
        this.detailSpot = detailSpot;
    }
}
