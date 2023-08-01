package com.example.matchup.matchupbackend.repository.team;

import com.example.matchup.matchupbackend.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface TeamRepositoryCustom {
    Slice<TeamSearchResponse> findTeamSliceByTeamRequest(TeamSearchRequest teamSearchRequest, Pageable pageable);
    TeamDetailResponse findTeamDetailByTeamID(Long teamID);
    MeetingSpot findMeetingSpotByTeamId(Long teamID);
}
