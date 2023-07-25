package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.TeamSearchResponse;
import com.example.matchup.matchupbackend.repository.TeamRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepositoryImpl teamRepository;

    public SliceTeamResponse TeamSearchListToSliceTeam(TeamSearchRequest teamSearchRequest, Pageable pageable)
    {
        Slice<TeamSearchResponse> teamSliceByTeamRequest = teamRepository.findTeamSliceByTeamRequest(teamSearchRequest, pageable);
        SliceTeamResponse sliceTeamResponse = new SliceTeamResponse(teamSliceByTeamRequest.getContent(),teamSliceByTeamRequest.getSize(),teamSliceByTeamRequest.hasNext());
        return sliceTeamResponse;
    }
}

