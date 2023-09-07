package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.mentoring.MentoringCardResponse;
import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.request.team.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.response.team.MeetingSpot;
import com.example.matchup.matchupbackend.dto.response.team.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamDetailResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamType;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

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
    public SliceTeamResponse showTeams(@Valid TeamSearchRequest teamSearchRequest, Pageable pageable) {
        return teamService.searchSliceTeamResponseList(teamSearchRequest, pageable);
    }

    @PostMapping("/team")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀 생성 및 저장")
    public Long makeTeam(@RequestHeader(value = "Authorization") String token, @Valid @RequestBody TeamCreateRequest teamCreateRequest) {
        Long userId = tokenProvider.getUserId(token, "TeamCreate");
        if (userId == null) {
            throw new AuthorizeException("TeamCreate");
        }
        log.info("제목: " + teamCreateRequest.getName() + "팀이 생성되었습니다");
        return teamService.makeNewTeam(userId, teamCreateRequest);
    }

    //팀 내용 업데이트
    @PutMapping("/team/{teamID}")
    @Operation(description = "팀 정보 수정") //인증 정보 추가돼서 팀장만 삭제할수 있도록 함
    public ResponseEntity<String> updateTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID, @Valid @RequestBody TeamCreateRequest teamCreateRequest) {
        Long userId = tokenProvider.getUserId(token, "updateTeam");
//        if (userId == null) {
//            throw new AuthorizeException("TeamUpdate");
//        }
        teamService.updateTeam(userId, teamID, teamCreateRequest);
        log.info("제목: " + teamCreateRequest.getName() + "팀이 수정되었습니다");
        return ResponseEntity.ok("업데이트 완료");
    }

    @DeleteMapping("/team/{teamID}")
    @Operation(description = "팀 삭제") //인증 정보 추가돼서 팀장만 삭제할수 있도록 함
    public ResponseEntity<String> deleteTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID) {
        Long userId = tokenProvider.getUserId(token, "deleteTeam");
        if (userId == null) {
            throw new AuthorizeException("TeamDelete");
        }
        teamService.deleteTeam(userId, teamID);
        log.info("teamID " + teamID.toString() + "팀이 삭제되었습니다");
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
    @Operation(description = "팀 상세페이지의 모임 타입 ex) 프로젝트 - 웹프로젝트")
    public TeamType showTeamType(@PathVariable Long teamID) {
        return teamService.getTeamType(teamID);
    }
}
