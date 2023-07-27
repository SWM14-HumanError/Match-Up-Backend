package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    @GetMapping("/api/v1/list/team")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "메인 페이지 API")
    public SliceTeamResponse showTeams(TeamSearchRequest teamSearchRequest, Pageable pageable)
    {
        return teamService.SearchSliceTeamList(teamSearchRequest, pageable);
    }

    @PostMapping("/api/v1/team")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀 생성 및 저장")
    public Long makeTeam(@RequestBody TeamCreateRequest teamCreateRequest)
    {
        return teamService.makeNewTeam(teamCreateRequest);
    }

    //업데이트
    @PatchMapping("/api/v1/{teamID}")
    @Operation(description = "팀 정보 수정")
    public ResponseEntity<String> updateTeam(@PathVariable Long teamID, @RequestBody TeamCreateRequest teamCreateRequest)
    {
        if(teamService.isUpdatable(teamID, teamCreateRequest) == false)//업데이트 불가능
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("기존 팀원 인원수 보다 높게 인원수를 설정하세요");
        }
        teamService.updateTeam(teamID, teamCreateRequest);
        return ResponseEntity.ok("업데이트 완료");
    }
}
