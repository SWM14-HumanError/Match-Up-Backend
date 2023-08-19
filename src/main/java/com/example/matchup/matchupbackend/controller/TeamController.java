package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.*;
import com.example.matchup.matchupbackend.dto.mentoring.MentoringCardResponse;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.TeamService;
import com.example.matchup.matchupbackend.service.TeamUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamService teamService;
    private final TokenProvider tokenProvider;

    @GetMapping("/list/team")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "메인 페이지 API")
    public SliceTeamResponse showTeams(TeamSearchRequest teamSearchRequest, Pageable pageable) {
        return teamService.searchSliceTeamResponseList(teamSearchRequest, pageable);
    }

    @PostMapping("/team")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀 생성 및 저장")
    public Long makeTeam(@RequestHeader(value = "Authorization") String token, @RequestBody TeamCreateRequest teamCreateRequest) {
        Long userId = getUserIdFromToken(token);
        return teamService.makeNewTeam(userId, teamCreateRequest);
    }

    //팀 내용 업데이트
    @PutMapping("/team/{teamID}")
    @Operation(description = "팀 정보 수정") //인증 정보 추가돼서 팀장만 삭제할수 있도록 함
    public ResponseEntity<String> updateTeam(@RequestHeader(value = "Authorization") String token, @PathVariable Long teamID, @RequestBody TeamCreateRequest teamCreateRequest) {
        Long userId = getUserIdFromToken(token);
        if (teamService.isUpdatable(teamID, teamCreateRequest) == false) //업데이트 불가능
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("기존 팀원 인원수 보다 높게 인원수를 설정하세요");
        }
        teamService.updateTeam(userId, teamID, teamCreateRequest);
        return ResponseEntity.ok("업데이트 완료");
    }

    @DeleteMapping("/team/{teamID}")
    @Operation(description = "팀 삭제") //인증 정보 추가돼서 팀장만 삭제할수 있도록 함
    public ResponseEntity<String> deleteTeam(@RequestHeader(value = "Authorization") String token, @PathVariable Long teamID) {
        Long userId = getUserIdFromToken(token);
        teamService.deleteTeam(userId, teamID);
        return ResponseEntity.ok("팀 삭제 완료");
    }

    @GetMapping("/team/{teamID}/info")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 제목,상세설명 API")
    public TeamDetailResponse showTeamInfo(@PathVariable Long teamID) {
        return teamService.getTeamInfo(teamID);
    }

    @GetMapping("/team/{teamID}/spot")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 모임 장소 API")
    public MeetingSpot showTeamMeetingSpot(@PathVariable Long teamID) {
        return teamService.getTeamMeetingSpot(teamID);
    }

    @GetMapping("/team/{teamID}/mentoring")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 멘토링 API")
    public List<MentoringCardResponse> showTeamMentoringList(@PathVariable Long teamID) {
        return teamService.getTeamMentoringCardList(teamID);
    }

    @GetMapping("/team/{teamID}/stacks")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 개발 스택 API")
    public List<String> showTeamTagList(@PathVariable Long teamID) {
        return teamService.getTeamTagStringList(teamID);
    }

    @GetMapping("/team/{teamID}/type")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 모임 카테고리")
    public TeamType showTeamType(@PathVariable Long teamID) {
        return teamService.getTeamType(teamID);
    }

    @GetMapping("/team/{teamID}/recruitInfo")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 팀원 모집 정보 불러오기 ")
    public TeamApprovedInfo showTeamRecruit(@PathVariable Long teamID) {
        return teamUserService.getTeamRecruitInfo(teamID);
    }

    private Long getUserIdFromToken(String token) {
        if (tokenProvider.validToken(token)) {
            return tokenProvider.getUserId(token);
        } else return null;
    }
}
