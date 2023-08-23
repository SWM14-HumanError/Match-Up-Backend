package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.TeamApprovedInfo;
import com.example.matchup.matchupbackend.dto.TeamUserCardResponse;
import com.example.matchup.matchupbackend.dto.teamuser.AcceptForm;
import com.example.matchup.matchupbackend.dto.teamuser.RecruitForm;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.TeamUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TeamUserController {
    private final TeamUserService teamUserService;
    private final TokenProvider tokenProvider;

    @GetMapping("/team/{teamID}/member")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 유저 API")
    public List<TeamUserCardResponse> showTeamUsers(@PathVariable Long teamID) {
        return teamUserService.getTeamUserCard(teamID);
    }

    @GetMapping("/team/{teamID}/recruitInfo")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 팀원 모집 정보 불러오기 ")
    public TeamApprovedInfo showTeamRecruit(@PathVariable Long teamID) {
        return teamUserService.getTeamRecruitInfo(teamID);
    }

    @PostMapping("/team/{teamID}/recruit")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "유저가 팀에 지원하는 API")
    public Long recruitToTeam(@RequestHeader(value = "Authorization") String token, @PathVariable Long teamID, @RequestBody RecruitForm recruitForm) {
        Long userID = getUserIdFromToken(token);
        log.info("userID: " + userID.toString());
        return teamUserService.recruitToTeam(userID, teamID, recruitForm);
    }

    @PostMapping("/team/{teamID}/acceptUser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀장이 유저를 팀원으로 수락하는 API")
    public String acceptUserToTeam(@RequestHeader(value = "Authorization") String token, @PathVariable Long teamID, @RequestBody AcceptForm acceptForm) {
        Long leaderID = getUserIdFromToken(token);
        log.info("leaderID: " + leaderID);
        teamUserService.acceptUserToTeam(leaderID, teamID, acceptForm);
        return "유저가 팀원이 되었습니다";
    }

    @DeleteMapping("/team/{teamID}/refuseUser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀장이 유저를 거절하는 API")
    public String refuseUserToTeam(@RequestHeader(value = "Authorization") String token, @PathVariable Long teamID, @RequestBody AcceptForm acceptForm) {
        Long leaderID = getUserIdFromToken(token);
        log.info("leaderID: " + leaderID);
        teamUserService.refuseUserToTeam(leaderID, teamID, acceptForm);
        return "안타깝게도 저희와 팀을 할수 없습니다";
    }

    @DeleteMapping("/team/{teamID}/kickUser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀장이 팀원을 강퇴하는 API")
    public String kickUserToTeam(@RequestHeader(value = "Authorization") String token, @PathVariable Long teamID, @RequestBody AcceptForm acceptForm) {
        Long leaderID = getUserIdFromToken(token);
        log.info("leaderID: " + leaderID);
        //teamUserService.refuseUserToTeam(leaderID, teamID, acceptForm);
        return "유저가 강퇴되었습니다";
    }

    private Long getUserIdFromToken(String token) {
        if (tokenProvider.validToken(token)) {
            return tokenProvider.getUserId(token);
        } else return null;
    }
}
