package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.profile.UserProfileEditRequest;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileDetailResponse;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileFeedbackResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamSearchResponse;
import com.example.matchup.matchupbackend.dto.response.userposition.UserPositionDetailResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateUserNicknameException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.feedback.FeedbackRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.user.UserSnsLinkRepository;
import com.example.matchup.matchupbackend.repository.userposition.UserPositionRepository;
import com.example.matchup.matchupbackend.repository.userprofile.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.matchup.matchupbackend.global.config.oauth.handler.OAuth2SuccessHandler.ACCESS_TOKEN_DURATION;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final TeamService teamService;
    private final TokenProvider tokenProvider;
    private final UserProfileRepository userProfileRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserSnsLinkRepository userSnsLinkRepository;
    private final FeedbackRepository feedbackRepository;

    public UserProfileDetailResponse showUserProfile(Long userId) {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("showUserProfile"));
        UserProfile userProfile = user.getUserProfile();
        List<TeamUser> teamUsers = user.getTeamUserList();
        List<UserPosition> userPositions = user.getUserPositions();

        return UserProfileDetailResponse.builder()
                .pictureUrl(user.getPictureUrl())
                .nickname(user.getNickname())
                .bestPositionLevel(user.getUserLevel())
                .snsLinks(userProfile.getUserSnsLinks().stream().collect(Collectors.toMap(UserSnsLink::getLinkType, UserSnsLink::getLinkUrl)))
                .isMentor(user.getIsMentor())
                .isAuth(user.getIsAuth())
                .lastLogin(user.getLastLogin())
                .introduce(userProfile.getIntroduce())
                .userPositions(userPositions.stream().map(position -> UserPositionDetailResponse.builder()
                        .positionLevel(position.getPositionLevel())
                        .positionName(position.getPositionName())
                        .build()).toList())
                .meetingAddress(userProfile.getMeetingAddress())
                .meetingTime(userProfile.getMeetingTime())
                .meetingType(userProfile.getMeetingType())
                .meetingNote(userProfile.getMeetingNote())
                .projects(teamUsers.stream()
                        .filter(TeamUser::getApprove)
                        .filter(teamUser -> teamUser.getTeam().getIsDeleted().equals(0L))
                        .filter(teamUser -> teamUser.getTeam().getType().equals(0L))
                            .map(teamUser -> TeamSearchResponse.from(
                                    teamUser.getTeam(),
                                    teamService.getUserMap())).toList())
                .studies(teamUsers.stream()
                        .filter(TeamUser::getApprove)
                        .filter(teamUser -> teamUser.getTeam().getIsDeleted().equals(0L))
                        .filter(teamUser -> teamUser.getTeam().getType().equals(1L))
                            .map(teamUser -> TeamSearchResponse.from(
                                    teamUser.getTeam(),
                                    teamService.getUserMap())).toList())
                .build();
    }

    private void checkDuplicateNickname(String nickname, Long userId) {
        User user;
        if (userId != null) {
            user = userRepository.findUserByNicknameAndIdNot(nickname, userId).orElse(null);
        } else {
            user = userRepository.findUserByNickname(nickname).orElse(null);
        }

        if (user != null) {
            throw new DuplicateUserNicknameException();
        }
    }

    @Transactional
    public String putUserProfile(String authorizationHeader, Long userId, UserProfileEditRequest request) {
        if (userId.equals(tokenProvider.getUserId(authorizationHeader, "프로필 수정을 위해 userId 검증 중"))) {
            checkDuplicateNickname(request.getNickname(), userId);
            Boolean isUnknown = tokenProvider.getUnknown(authorizationHeader, "유저 프로필을 수정하는 로직을 실행 중입니다.");

            User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("유저 프로필 수정 중, 유저를 찾을 수 없다."));
            UserProfile userProfile = user.getUserProfile();

            // UserPosition update
            userPositionRepository.deleteAllByUser(user);
            if (request.getUserPositionLevels() != null) {
                List<UserPosition> userPositions = request.getUserPositionLevels().entrySet().stream().map(positionName -> UserPosition.builder().positionName(positionName.getKey()).positionLevel(positionName.getValue()).user(user).build()).collect(Collectors.toCollection(ArrayList::new));
                userPositionRepository.saveAll(userPositions);
            }

            // UserSnsLink update
            // todo: request DTO 필드가 널이면 아래와 같이 가정문으로 해결할 수밖에 없나?
            if (userProfile == null || userProfile.getId() == null) {
                userProfile = UserProfile.builder().user(user).build();
                userProfileRepository.save(userProfile);
            }
            userSnsLinkRepository.deleteByUserProfile(userProfile);
            if (request.getLink() != null) {
                List<UserSnsLink> userSnsLinks = request.getLink().entrySet().stream().map(type -> UserSnsLink.builder().linkType(type.getKey()).linkUrl(type.getValue()).userProfile(user.getUserProfile()).build()).toList();
                userSnsLinkRepository.saveAll(userSnsLinks);
            }

            // UserProfile update
            user.getUserProfile().updateUserProfile(request);

            // User update
            user.updateUserProfile(request);

            return isUnknown ? tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION) : null;
        }
        else {
            throw new AuthorizeException("토큰에 저장된 회원 정보와 요청한 프로필 회원 정보가 달라서 수정할 수 없습니다.");
        }
    }

    /**
     * 유저 프로필 / 피드백 목록을 List<string>으로 보여주는 매서드
     * @param userId: Long
     * @param grade: String
     */
    public UserProfileFeedbackResponse getUserProfileFeedbacks(Long userId, String grade) {
       List<Feedback> feedbacks = feedbackRepository.findUserFeedbackByGrade(userId, grade);
       return UserProfileFeedbackResponse.from(feedbacks);
    }

    /**
     * 피드백 목록을 숨김/공개 설정하는 매서드
     * @param userId: Long
     */
    @Transactional
    public String hideFeedbacks(Long userId) {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("피드백 숨김 여부 설정에서 유저를 가져올수 없습니다."));
        user.changeFeedbackHide();
        return "피드백 공개 여부가 " + (user.getFeedbackHider() ? "숨김" : "공개") + " 처리 되었습니다.";
    }

    public void isPossibleNickname(String nickname, String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "닉네임 중복검사를 하고 있습니다.");
        checkDuplicateNickname(nickname, userId);
    }
}
