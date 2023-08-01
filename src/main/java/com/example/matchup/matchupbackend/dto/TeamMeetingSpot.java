package com.example.matchup.matchupbackend.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamMeetingSpot {
    private String onOffline;
    private String city;
    private String detailSpot;

    @QueryProjection
    public TeamMeetingSpot(String onOffline, String city, String detailSpot) {
        this.onOffline = onOffline;
        this.city = city;
        this.detailSpot = detailSpot;
    }
}
