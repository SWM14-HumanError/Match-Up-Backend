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
                .projects(teamUsers.stream().filter(teamUser -> teamUser.getTeam().getType().equals(0L))
                        .map(teamUser -> TeamSearchResponse.from(
                                teamUser.getTeam(),
                                teamService.getUserMap())).toList())
                .studies(teamUsers.stream().filter(teamUser -> teamUser.getTeam().getType().equals(1L))
                        .map(teamUser -> TeamSearchResponse.from(
                                teamUser.getTeam(),
                                teamService.getUserMap())).toList())
                .build();
    }

    public void checkDuplicateNickname(String nickname) {
        User user = userRepository.findUserByNickname(nickname).orElse(null);
        if (user != null) {
            throw new DuplicateUserNicknameException();
        }
    }

    @Transactional
    public String putUserProfile(String token, Long userId, UserProfileEditRequest request) {
        if (userId.equals(tokenProvider.getUserId(token, "프로필 수정을 위해 userId 검증 중"))) {
            checkDuplicateNickname(request.getNickname());
            Boolean isUnknown = tokenProvider.getUnknown(token, "유저 프로필을 수정하는 로직을 실행 중입니다.");

            User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("유저 프로필 수정 중, 유저를 찾을 수 없다."));
            User updatedUser = user.updateUserProfile(request);
            List<UserPosition> userPositions = user.getUserPositions().stream().map(position -> position.updateUserProfile(request.getUserPositionLevels())).toList();
            UserProfile userProfile = user.getUserProfile().updateUserProfile(request);
            userSnsLinkRepository.deleteByUserProfile(userProfile);
            request.getLink().entrySet().stream().map(type -> UserSnsLink.builder().linkType(type.getKey()).linkUrl(type.getValue()).userProfile(userProfile).build()).toList();

            String newToken = isUnknown ? tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION) : null;

            userRepository.save(updatedUser);
            userProfileRepository.save(userProfile);
            userPositionRepository.saveAll(userPositions);

            return newToken;
        }
        else {
            throw new AuthorizeException("토큰에 저장된 회원 정보와 요청한 프로필 회원 정보가 달라서 수정할 수 없습니다.");
        }
    }

    public UserProfileFeedbackResponse getUserProfileFeedbacks(Long userId, String grade) {
       List<Feedback> feedbacks = feedbackRepository.findUserFeedbackByGrade(userId, grade);
       return UserProfileFeedbackResponse.from(feedbacks);
    }
}
