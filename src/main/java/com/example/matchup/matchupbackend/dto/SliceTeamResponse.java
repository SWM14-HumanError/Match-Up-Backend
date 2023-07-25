package com.example.matchup.matchupbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;
@Data
@AllArgsConstructor
public class SliceTeamResponse {
    private List<TeamSearchResponse> teamSearchResponseList;
    private int size;
    private Boolean hasNextSlice;
}
