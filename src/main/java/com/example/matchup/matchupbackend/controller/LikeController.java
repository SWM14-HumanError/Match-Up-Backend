package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.response.team.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.dto.response.user.UserLikeResponse;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final TokenProvider tokenProvider;
    private final LikeService likeService;
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "유저에게 좋아요 누르는 API")
    public String saveLikeToUser(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long userId) {
        Long likeGiverID = tokenProvider.getUserId(authorizationHeader, "giveLikeToUser");
        likeService.saveLikeToUser(likeGiverID, userId);
        log.info("userID: " + userId + " 에게 좋아요를 눌렀습니다.");
        return "userID: " + userId + " 에게 좋아요를 눌렀습니다.";
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "유저에게 좋아요 지우는 API")
    public String deleteLikeToUser(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, @PathVariable Long userId) {
        Long likeGiverID = tokenProvider.getUserId(authorizationHeader, "deleteLikeToUser");
        likeService.deleteLikeToUser(likeGiverID, userId);
        log.info("userID: " + userId + " 에게 준 좋아요를 삭제하였습니다.");
        return "userID: " + userId + " 에게 준 좋아요를 삭제하였습니다.";
    }


    @GetMapping("/check/{userID}")
    @ResponseStatus(HttpStatus.OK)
    public UserLikeResponse checkUserLike(@RequestHeader(value = HEADER_AUTHORIZATION, required = false) String authorizationHeader,
                                          @PathVariable("userID") Long userID) {
        Long likeGiverID = tokenProvider.getUserId(authorizationHeader, "checkUserLike");
        return likeService.checkUserLiked(likeGiverID, userID);
    }

    @GetMapping("/mylike/project")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "내가 좋아요 누른 프로젝트를 보는 API")
    public SliceTeamResponse getLikedProjectTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, Pageable pageable) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getLikedProjectTeam");
        return likeService.getLikedSliceProjectTeamResponse(userId, pageable);
    }

    @GetMapping("/mylike/study")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "내가 좋아요 누른 스터디를 보는 API")
    public SliceTeamResponse getLikedStudyTeam(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, Pageable pageable) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getLikedStudyTeam");
        return likeService.getLikedSliceStudyTeamResponse(userId, pageable);
    }

    @GetMapping("/mylike/user")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "내가 좋아요 누른 유저를 보는 API")
    public SliceUserCardResponse getLikedUser(@RequestHeader(value = HEADER_AUTHORIZATION) String authorizationHeader, Pageable pageable) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getLikedUser");
        return likeService.getLikedSliceUserCardResponse(userId, pageable);
    }
}
