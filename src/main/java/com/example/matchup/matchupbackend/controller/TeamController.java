package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.TeamDetailResponse;
import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    @GetMapping("/list/team")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "메인 페이지 API")
    public SliceTeamResponse showTeams(TeamSearchRequest teamSearchRequest, Pageable pageable)
    {
        return teamService.SearchSliceTeamList(teamSearchRequest, pageable);
    }

    @PostMapping("/team")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀 생성 및 저장")
    public Long makeTeam(@RequestBody TeamCreateRequest teamCreateRequest)
    {
        return teamService.makeNewTeam(teamCreateRequest);
    }

    //팀 내용 업데이트
    @PutMapping("team/{teamID}")
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

    @DeleteMapping("team/{teamID}")
    @Operation(description = "팀 삭제")
    public ResponseEntity<String> deleteTeam(@PathVariable Long teamID)
    {
        teamService.deleteTeam(teamID);
        return ResponseEntity.ok("팀 삭제 완료");
    }


//    @GetMapping("team/{teamID}")
//    @Operation(description = "팀 정보 불러오기")
//    public ResponseEntity<TeamDetailResponse> showTeamDetails(@PathVariable Long teamID)
//    {
//
//    }
}
