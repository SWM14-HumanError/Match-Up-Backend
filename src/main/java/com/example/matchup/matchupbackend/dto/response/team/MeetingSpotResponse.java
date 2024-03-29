package com.example.matchup.matchupbackend.dto.response.team;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeetingSpotResponse {
    @NotBlank(message = "온라인인지 오프라인인지 정해주세요")
    private String onOffline;
    private String city;
    private String detailSpot;

    @Builder
    @QueryProjection
    public MeetingSpotResponse(String onOffline, String city, String detailSpot) {
        this.onOffline = onOffline;
        this.city = city;
        this.detailSpot = detailSpot;
    }
}
