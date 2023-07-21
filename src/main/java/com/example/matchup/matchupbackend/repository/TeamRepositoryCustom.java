package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.dto.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.TeamSearchResponse;
import com.example.matchup.matchupbackend.entity.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface TeamRepositoryCustom {
    Slice<TeamSearchResponse> findTeamSliceByTeamRequest(TeamSearchRequest teamSearchRequest, Pageable pageable);
}
