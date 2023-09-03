package com.example.matchup.matchupbackend.dto.response.team;

import com.example.matchup.matchupbackend.dto.TeamSearchResponse;
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
