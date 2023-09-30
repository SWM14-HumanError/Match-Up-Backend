package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.UserCardResponse;
import com.example.matchup.matchupbackend.dto.request.user.AdditionalUserInfoRequest;
import com.example.matchup.matchupbackend.dto.request.user.SuggestInviteMyTeamRequest;
import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.dto.response.user.InviteMyTeamInfoResponse;
import com.example.matchup.matchupbackend.dto.response.user.InviteMyTeamResponse;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.ExpiredTokenException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.config.jwt.TokenService;
import com.example.matchup.matchupbackend.repository.alert.AlertRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.userposition.UserPositionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_PERMITTED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;
    private final UserPositionRepository userPositionRepository;
    private final AlertRepository alertRepository;

    public SliceUserCardResponse searchSliceUserCard(UserSearchRequest userSearchRequest, Pageable pageable) {
        Slice<User> userListByUserRequest = userRepository.findUserListByUserRequest(userSearchRequest, pageable);
        SliceUserCardResponse sliceUserCardResponse = SliceUserCardResponse.builder()
                .userCardResponses(userCardResponseList(userListByUserRequest.getContent()))
                .size(userListByUserRequest.getSize())
                .hasNextSlice(userListByUserRequest.hasNext())
                .build();
        return sliceUserCardResponse;
    }

    public List<UserCardResponse> userCardResponseList(List<User> userList) {
        return userList.stream().map(
                user -> {
                    Position position = new Position(user.getPosition(), user.getPositionLevel());
                    return UserCardResponse.of(user.getId(), user.getPictureUrl(), user.getUserLevel(),
                            user.getNickname(), position, user.getFeedbackScore(),
                            user.getLikes(), user.returnStackList());
                }
        ).collect(Collectors.toList());
    }

    @Transactional
    public Long saveAdditionalUserInfo(String authorizationHeader, AdditionalUserInfoRequest request) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "saveAdditionalUserInfo");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("유저의 아이디를 찾을 수 없습니다."));;
        if (!user.getIsFirstLogin()) throw new ResourceNotPermitException(NOT_PERMITTED, "이미 초기 정보를 제공한 사용자입니다.");

        userPositionRepository.deleteAllByUser(user); // 밀어버리기 위해서 사용. 만약 소개먼저 작성한 유저가 있을 수도 있음

        // todo: 여기서 UnsupportedOperationException 에러 발생: 현재는 immutable list라는 것으로 이해하고 있지만 왜 인지는 모르겟음
        //  List<UserPosition> userPositions = request.getUserPositionLevels().entrySet().stream().map(positionName -> UserPosition.builder().positionName(positionName.getKey()).positionLevel(positionName.getValue()).user(user).build()).toList();

        List<UserPosition> userPositions = request.getUserPositionLevels().entrySet().stream().map(positionName -> UserPosition.builder().positionName(positionName.getKey()).positionLevel(positionName.getValue()).user(user).build()).collect(Collectors.toCollection(ArrayList::new));
        User updatedUser = user.updateFirstLogin(request, userPositions);

        userRepository.save(updatedUser);
        userPositionRepository.saveAll(userPositions);

        return userId;
    }

    public User findRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected refresh token."));
    }

    public String tokenRefresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String refreshToken = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("refresh_token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            return tokenService.createNewAccessToken(refreshToken);
        } else {
            throw new ExpiredTokenException("만료된 refresh 토큰으로 접근하였거나 토큰이 없습니다.");
        }
    }

    @Transactional
    public void userAgreeTermOfService(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "유저의 이용약관 동의를 저장하고 있습니다.");
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("유저의 이용약관 동의를 저장하면서 존재하지 않는 유저 id로 요청했습니다."));

        user.updateTermService();
    }

    @Transactional
    public void updateUserLastLogin(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "유저 온라인 상태를 확인하면서 유효하지 않은 토큰을 받았습니다.");
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("유저 온라인 상태를 확인하면서 존재하지 않는 유저 id를 받았습니다."));

        user.updateUserLastLogin();
    }

     // todo 쿼리문으로 UserProfile도 조회하는 문제가 있다.

    /**
     * 내 프로젝트에 초대하기에 필요한 프로젝트 목록을 조회합니다.
     */
    public InviteMyTeamResponse getInviteMyTeam(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "내 프로젝트에 초대하기를 조회하고 있습니다.");
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("내 프로젝트에 초대하기를 조회하는 과정에서 존재하지 않는 유저 id를 요청했습니다."));
        List<TeamUser> teamUsers = user.getTeamUserList().stream().filter(teamUser -> (teamUser != null && teamUser.getApprove() != null &&teamUser.getApprove())).toList();

        List<InviteMyTeamInfoResponse> response = teamUsers.stream()
                .map(TeamUser::getTeam)
                .filter(team -> team.getIsDeleted().equals(0L))
                .map(team -> InviteMyTeamInfoResponse.builder().team(team).build())
                .toList();
        return new InviteMyTeamResponse(response);
    }

    /**
     * 내 프로젝트에 초대하기에 지원할 때 필요한 검증과 대상자 알림에 등록합니다.
     */
    @Transactional
    public void postInviteMyTeam(String authorizationHeader, SuggestInviteMyTeamRequest request) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "내 프로젝트에 초대하기를 지원하고 있습니다.");
        User suggester = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("내 프로젝트에 초대하기를 지원하는 과정에서 존재하지 않는 제안 유저 id를 요청했습니다."));
        User receiver = userRepository.findUserById(request.getReceiverId()).orElseThrow(() -> new UserNotFoundException("내 프로젝트에 초대하기를 지원하는 과정에서 존재하지 않는 제안 유저 id를 요청했습니다."));
        Team team = teamRepository.findTeamById(request.getTeamId()).orElseThrow(() -> new TeamNotFoundException("내 프로젝트에 초대하기를 지원하는 과정에서 존재하지 않는 팀 id로 요청했습니다."));

        isProperSuggestMyTeam(team, suggester, receiver);

        alertRepository.save(Alert.builder()
                .title(team.getType().equals(0L) ? "프로젝트에 초대받았습니다." : "스터디에 초대받았습니다.")
                .redirectUrl("/team/" + request.getTeamId())
                .content(request.getContent())
                .alertType(team.getType().equals(0L) ? AlertType.PROJECT : AlertType.STUDY)
                .user(receiver)
                .build());
    }

    private void isProperSuggestMyTeam(Team team, User suggester, User receiver) {
        if (!team.getIsDeleted().equals(0L)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "내 프로젝트에 초대하기를 지원하면서 삭제된 팀으로 요청했습니다.");
        }

        if (team.getTeamUserList().stream().noneMatch(teamUser -> teamUser.getUser().equals(suggester) && teamUser.getApprove())) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "내 프로젝트에 초대하기를 지원하면서 초대할 수 없는 팀원이 요청했습니다.");
        }

        if (team.getTeamUserList().stream().anyMatch(teamUser -> teamUser.getUser().equals(receiver))) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "내 프로젝트에 초대하기를 지원하면서 이미 합류된 팀원이나 자기 자신에게 초대를 보냈습니다.");
        }
    }
}