package com.example.matchup.matchupbackend.dto.response.team;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class SliceTeamResponse {
    private List<TeamSearchResponse> teamSearchResponseList;
    private int size;
    private Boolean hasNextSlice;

}
