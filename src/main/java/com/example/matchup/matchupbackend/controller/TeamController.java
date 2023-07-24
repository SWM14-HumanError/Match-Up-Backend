package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    @GetMapping("/api/v1/list/team")
    public SliceTeamResponse showTeams(TeamSearchRequest teamSearchRequest, Pageable pageable)
    {
        return teamService.TeamSearchListToSliceTeam(teamSearchRequest, pageable);
    }
}
