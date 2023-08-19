package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.TeamApprovedInfo;
import com.example.matchup.matchupbackend.dto.TeamUserCardResponse;
import com.example.matchup.matchupbackend.dto.teamuser.RecruitForm;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.TeamUserService;
import io.jsonwebtoken.Claims;
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
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "유저가 팀에 지원하는 API")
    public Long recruitToTeam(@RequestHeader(value = "Authorization") String token, @PathVariable Long teamID, @RequestBody RecruitForm recruitForm) {
        Long userID = getUserIdFromToken(token);
        return teamUserService.recruitToTeam(userID, teamID, recruitForm);
    }

    private Long getUserIdFromToken(String token) {
        if (tokenProvider.validToken(token)) {
            return tokenProvider.getUserId(token);
        } else return null;
    }
}
