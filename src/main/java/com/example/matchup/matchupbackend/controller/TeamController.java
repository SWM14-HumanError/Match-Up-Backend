package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    @GetMapping("/api/v1/list/team")
    @Operation(description = "메인 페이지 API")
    public SliceTeamResponse showTeams(TeamSearchRequest teamSearchRequest, Pageable pageable)
    {
        return teamService.SearchSliceTeamList(teamSearchRequest, pageable);
    }

    @PostMapping("/api/v1")
    @Operation(description = "팀 생성 및 저장")
    public Long makeTeam(@RequestBody TeamCreateRequest teamCreateRequest)
    {
        return teamService.makeNewTeam(teamCreateRequest);
    }
}
