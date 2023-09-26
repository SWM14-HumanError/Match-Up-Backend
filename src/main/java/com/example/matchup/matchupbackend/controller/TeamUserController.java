package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.teamuser.AcceptFormRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.RecruitFormRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.TeamUserFeedbackRequest;
import com.example.matchup.matchupbackend.dto.response.teamuser.RecruitInfoResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamApprovedInfoResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamUserCardResponse;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.TeamUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public List<TeamUserCardResponse> showTeamUsers(@Nullable @RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long teamID) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "showTeamUsers");
        return teamUserService.getTeamUserCard(userId, teamID);
    }

    @GetMapping("/team/{teamID}/recruitInfo")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 팀원 모집 정보 불러오기 ")
    public TeamApprovedInfoResponse showTeamRecruit(@PathVariable Long teamID) {
        return teamUserService.getTeamApprovedMemberInfo(teamID);
    }

    @PostMapping("/team/{teamID}/recruit")
    @Operation(description = "유저가 팀에 지원하는 API")
    public ResponseEntity<Long> recruitToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long teamID, @Valid @RequestBody RecruitFormRequest recruitForm) {
        Long userID = tokenProvider.getUserId(authorizationHeader, "recruitToTeam");
        if (userID == null) {
            throw new AuthorizeException("TeamUserRecruit");
        }
        log.info("userID: " + userID);
        return ResponseEntity.created(URI.create("/team/" + teamID)).body(teamUserService.recruitToTeam(userID, teamID, recruitForm));
    }

    @PostMapping("/team/{teamID}/acceptUser")
    @Operation(description = "팀장이 유저를 팀원으로 수락하는 API")
    public ResponseEntity<String> acceptUserToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long teamID, @Valid @RequestBody AcceptFormRequest acceptForm) {
        Long leaderID = tokenProvider.getUserId(authorizationHeader, "acceptUserToTeam");
        if (leaderID == null) {
            throw new AuthorizeException("TeamUserAccept");
        }
        log.info("leaderID: " + leaderID);
        teamUserService.acceptUserToTeam(leaderID, teamID, acceptForm);
        return ResponseEntity.created(URI.create("/team/" + teamID)).body("유저가 팀원이 되었습니다");
    }

    @DeleteMapping("/team/{teamID}/refuseUser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀장이 유저를 거절하는 API")
    public String refuseUserToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long teamID, @Valid @RequestBody AcceptFormRequest acceptForm) {
        Long leaderID = tokenProvider.getUserId(authorizationHeader, "refuseUserToTeam");
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
    public String kickUserToTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long teamID, @Valid @RequestBody AcceptFormRequest acceptForm) {
        Long leaderID = tokenProvider.getUserId(authorizationHeader, "kickUserToTeam");
        if (leaderID == null) {
            throw new AuthorizeException("TeamUserKick");
        }
        log.info("leaderID: " + leaderID);
        teamUserService.kickUserToTeam(leaderID, teamID, acceptForm);
        return "유저가 강퇴되었습니다";
    }

    @PostMapping("/team/{teamID}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀 구성원끼리 피드백을 주는 API")
    public String feedbackEachTeamUser(@RequestHeader(value = HEADER_AUTHORIZATION) String token, @PathVariable Long teamID, @Valid @RequestBody TeamUserFeedbackRequest feedback) {
        Long giverId = tokenProvider.getUserId(token, "feedbackTeamUser");
        if (giverId == null) {
            throw new AuthorizeException("feedbackTeamUser");
        }
        teamUserService.feedbackToTeamUser(giverId, teamID, feedback);
        log.info("userId: " + giverId + " -> " + "userId: " + feedback.getReceiverID() + " 의 피드백이 추가 되었습니다");
        return "유저에게 평점을 주었습니다";
    }

    @GetMapping("/team/{teamID}/recruit/{recruitID}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "지원서 열람 하는 API")
    public RecruitInfoResponse showRecruit(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long teamID, @PathVariable Long recruitID) {
        Long userID = tokenProvider.getUserId(authorizationHeader, "showRecruit");
        if (userID == null) {
            throw new AuthorizeException("TeamUserRecruit");
        }
        return teamUserService.getRecruitInfo(userID, teamID, recruitID);
    }
}
