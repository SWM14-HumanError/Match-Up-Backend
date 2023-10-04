package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.UserCardResponse;
import com.example.matchup.matchupbackend.dto.request.user.AdditionalUserInfoRequest;
import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.dto.response.user.InviteMyTeamInfoResponse;
import com.example.matchup.matchupbackend.dto.response.user.InviteMyTeamResponse;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.entity.TeamUser;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.ExpiredTokenException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.config.jwt.TokenService;
import com.example.matchup.matchupbackend.global.util.CookieUtil;
import com.example.matchup.matchupbackend.repository.alert.AlertRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.userposition.UserPositionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.matchup.matchupbackend.global.config.oauth.handler.OAuth2SuccessHandler.REFRESH_TOKEN_COOKIE_NAME;
import static com.example.matchup.matchupbackend.global.config.oauth.handler.OAuth2SuccessHandler.REFRESH_TOKEN_DURATION;

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

            String accessToken = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            return tokenService.createNewAccessToken(refreshToken, accessToken);
        } else {
            throw new ExpiredTokenException("만료된 refresh 토큰으로 접근하였거나 토큰이 없습니다.");
        }
    }

    @Transactional
    public String userAgreeTermOfService(String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("유저의 이용약관 동의를 저장하면서 존재하지 않는 유저 이메일로 요청했습니다."));
        if (!user.getAgreeTermOfServiceId().equals(id)) {
            throw new AuthorizeException("이용약관을 동의하면서 유효하지 않은 id값으로 요청했습니다.");
        }

        user.updateTermService();
        return tokenProvider.generateToken(user, Duration.ofHours(2));
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
     * OAuth2SuccessHandler 로그인에 성공하면
     * Refresh 토큰을 User 엔터티에 저장
     * 그리고 RefreshToken 쿠키에 저장
     */
    @Transactional
    public User saveRefreshToken(HttpServletRequest request, HttpServletResponse response, String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("로그인에 성공하면서 Authorization 서버로부터 받은 이메일과 다른 이메일로 요청했습니다."));
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

        user.updateNewRefreshToken(refreshToken, id);
        addRefreshTokenToCookie(request, response, refreshToken);

        return user;
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }
}