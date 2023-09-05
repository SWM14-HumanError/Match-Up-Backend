package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.TeamApprovedInfoResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamUserCardResponse;
import com.example.matchup.matchupbackend.dto.request.teamuser.AcceptFormRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.RecruitFormRequest;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.TeamUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TeamUserController {
    private final TeamUserService teamUserService;
    private final TokenProvider tokenProvider;

    @GetMapping("/team/{teamID}/member")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 유저 API - 팀장(수락+비수락)")
    public List<TeamUserCardResponse> showTeamUsers(@Nullable @RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID) {
        Long userId = tokenProvider.getUserId(token);
        return teamUserService.getTeamUserCard(userId, teamID);
    }

    @GetMapping("/team/{teamID}/recruitInfo")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 팀원 모집 정보 불러오기 ")
    public TeamApprovedInfoResponse showTeamRecruit(@PathVariable Long teamID) {
        return teamUserService.getTeamApprovedMemberInfo(teamID);
    }

    @PostMapping("/team/{teamID}/recruit")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "유저가 팀에 지원하는 API")
    public Long recruitToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID, @Valid @RequestBody RecruitFormRequest recruitForm) {
        Long userID = tokenProvider.getUserId(token);
        if (userID == null) {
            throw new AuthorizeException("TeamUserRecruit");
        }
        log.info("userID: " + userID);
        return teamUserService.recruitToTeam(userID, teamID, recruitForm);
    }

    @PostMapping("/team/{teamID}/acceptUser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀장이 유저를 팀원으로 수락하는 API")
    public String acceptUserToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID, @Valid @RequestBody AcceptFormRequest acceptForm) {
        Long leaderID = tokenProvider.getUserId(token);
        if (leaderID == null) {
            throw new AuthorizeException("TeamUserAccept");
        }
        log.info("leaderID: " + leaderID);
        teamUserService.acceptUserToTeam(leaderID, teamID, acceptForm);
        return "유저가 팀원이 되었습니다";
    }

    @DeleteMapping("/team/{teamID}/refuseUser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀장이 유저를 거절하는 API")
    public String refuseUserToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID, @Valid @RequestBody AcceptFormRequest acceptForm) {
        Long leaderID = tokenProvider.getUserId(token);
        if (leaderID == null) {
            throw new AuthorizeException("TeamUserRefuse");
        }
        log.info("leaderID: " + leaderID);
        teamUserService.refuseUserToTeam(leaderID, teamID, acceptForm);
        return "안타깝게도 저희와 팀을 할수 없습니다";
    }

    @DeleteMapping("/team/{teamID}/kickUser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀장이 팀원을 강퇴하는 API")
    public String kickUserToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID, @Valid @RequestBody AcceptFormRequest acceptForm) {
        Long leaderID = tokenProvider.getUserId(token);
        if (leaderID == null) {
            throw new AuthorizeException("TeamUserKick");
        }
        log.info("leaderID: " + leaderID);
        teamUserService.kickUserToTeam(leaderID, teamID, acceptForm);
        return "유저가 강퇴되었습니다";
    }

//    private Long getUserIdFromToken(String token) {
//        if (tokenProvider.validToken(token)) {
//            return tokenProvider.getUserId(token);
//        } else return null;
//    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    return tokenProvider.getUserId(token);
                }
            }
        } return null;
    }
}
