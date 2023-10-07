package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.user.ProfileRequest;
import com.example.matchup.matchupbackend.dto.request.user.ProfileTagPositionRequest;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileDetailResponse;
import com.example.matchup.matchupbackend.dto.response.profile.UserProfileFeedbackResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamSearchResponse;
import com.example.matchup.matchupbackend.dto.response.userposition.UserPositionDetailResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateUserNicknameException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.feedback.FeedbackRepository;
import com.example.matchup.matchupbackend.repository.tag.TagRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.user.UserSnsLinkRepository;
import com.example.matchup.matchupbackend.repository.userposition.UserPositionRepository;
import com.example.matchup.matchupbackend.repository.userprofile.UserProfileRepository;
import com.example.matchup.matchupbackend.repository.usertag.UserTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final TeamService teamService;
    private final TokenProvider tokenProvider;
    private final UserProfileRepository userProfileRepository;
    private final UserTagRepository userTagRepository;
    private final TagRepository tagRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserSnsLinkRepository userSnsLinkRepository;
    private final FeedbackRepository feedbackRepository;

    public UserProfileDetailResponse showUserProfile(Long userId) {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("사용자 프로필을 조회하는 과정에서 존재하지 않는 user id로 요청했습니다."));
        UserProfile userProfile = user.getUserProfile();
        List<TeamUser> teamUsers = user.getTeamUserList().stream()
                .filter(teamUser -> teamUser.getApprove() && teamUser.getTeam().getIsDeleted().equals(0L))
                .toList();
        List<UserPosition> userPositions = user.getUserPositions();
        List<UserTag> userTags = userTagRepository.findAllByUser(user);
        Set<RoleType> tagSet = userTags.stream()
                .map(UserTag::getType)
                .collect(Collectors.toSet());
        Map<RoleType, List<String>> tagMap = tagSet.stream()
                .collect(Collectors.toMap(
                        t -> t,
                        tag -> userTags.stream()
                                .filter(userTag -> userTag.getType() == tag)
                                .map(UserTag::getTagName)
                                .toList()
                ));

        return UserProfileDetailResponse.builder()
                .pictureUrl(user.getPictureUrl())
                .nickname(user.getNickname())
                .bestPositionLevel(user.getUserLevel())
                .feedbackScore(user.getFeedbackScore())
                .snsLinks(userProfile.getUserSnsLinks().stream()
                        .collect(Collectors.toMap(UserSnsLink::getLinkType, UserSnsLink::getLinkUrl)))
                .isMentor(user.getIsMentor())
                .isAuth(user.getIsAuth())
                .lastLogin(ZonedDateTime.of(user.getLastLogin(), ZoneId.of("Asia/Seoul")))
                .introduce(userProfile.getIntroduce())
                .userPositions(userPositions.stream().map(
                        position -> UserPositionDetailResponse.builder()
                                .type(position.getType())
                                .typeLevel(position.getTypeLevel())
                                .tags(tagMap.get(position.getType()))
                                .build())
                        .toList())
                .meetingAddress(userProfile.getMeetingAddress())
                .meetingTime(userProfile.getMeetingTime())
                .meetingType(userProfile.getMeetingType())
                .meetingNote(userProfile.getMeetingNote())
                .projects(teamUsers.stream()
                        .filter(teamUser -> teamUser.getTeam().getType().equals(0L))
                            .map(teamUser -> TeamSearchResponse.from(
                                    teamUser.getTeam(),
                                    teamService.getUserMap()))
                        .toList())
                .studies(teamUsers.stream()
                        .filter(teamUser -> teamUser.getTeam().getType().equals(1L))
                            .map(teamUser -> TeamSearchResponse.from(
                                    teamUser.getTeam(),
                                    teamService.getUserMap())).
                        toList())
                .build();
    }

    @Transactional
    public void putUserProfile(String authorizationHeader, Long userId, ProfileRequest request) {
        if (!userId.equals(tokenProvider.getUserId(authorizationHeader, "프로필 수정을 위해 userId 검증 중 일치하지 않는 유저 아이디 값으로 요청했습니다."))) {
            throw new AuthorizeException("토큰에 저장된 회원 정보와 요청한 프로필 회원 정보가 달라서 수정할 수 없습니다.");
        }

        checkDuplicateNickname(request.getNickname(), userId);
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("유저 프로필 수정 중, 유저를 찾을 수 없다."));
        UserProfile userProfile = user.getUserProfile();

        updateUserPositions(request, user);
        updateUserTags(request, user);

        // UserSnsLink update
        // todo: request DTO 필드가 널이면 아래와 같이 가정문으로 해결할 수밖에 없나?
        /*
         * 계정을 새로 만들거나 소개를 작성하지 않으면 userProfile 없을 수도 있기 때문에
         * user 연결된 userProfile 생성한다.
         */
//
//        // 요구사항 변경으로 userProfile은 반드시 생성된다.
//        if (userProfile == null || userProfile.getId() == null) {
//            userProfile = UserProfile.builder().user(user).build();
//            userProfileRepository.save(userProfile);
//        }

        userSnsLinkRepository.deleteByUserProfile(userProfile);
        if (request.getLink() != null) {
            List<UserSnsLink> userSnsLinks = request.getLink().entrySet().stream()
                    .map(type -> UserSnsLink.builder()
                        .linkType(type.getKey())
                        .linkUrl(type.getValue())
                        .userProfile(user.getUserProfile())
                        .build()).toList();
            userSnsLinkRepository.saveAll(userSnsLinks);
        }

        // UserProfile update
        if (userProfile == null) {
            throw new NotFoundException("회원가입이 완료되지 않은 회원의 접근입니다.");
        }
        userProfile.updateUserProfile(request);

        // User update
        user.updateUserProfile(request);
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

    private void updateUserPositions(ProfileRequest request, User user) {
        // UserPosition update
        // 이미 있는 기술 스택 레벨 변경하기
        List<UserPosition> userPositions = userPositionRepository.findAllByUser(user);
        Map<RoleType, Integer> userPositionMap = request.getProfileTagPositions().stream()
                .collect(Collectors.toMap(ProfileTagPositionRequest::getType, ProfileTagPositionRequest::getTypeLevel));
        userPositions.stream() // todo 업데이트 되는 지 테스트
                .filter(userPosition -> request.getTypeList().contains(userPosition.getType()))
                .forEach(userPosition -> userPosition.updateUserPosition(userPositionMap.get(userPosition.getType())));

        // 없는 기술 스택 레벨 변경하기
        List<RoleType> roleTypes = userPositions.stream()
                .map(UserPosition::getType)
                .toList();
        List<UserPosition> newUserPositions = request.getProfileTagPositions().stream()
                .filter(profileTagPosition -> !roleTypes.contains(profileTagPosition.getType()))
                .map(profileTagPosition -> UserPosition.createWhenProfileUpdate(profileTagPosition, user))
                .toList();
        userPositionRepository.saveAll(newUserPositions);

        //        userPositionRepository.deleteAllByUser(user);
//        if (request.getProfileTagPositionRequests() != null) {
//            List<UserPosition> userPositions = request.getProfileTagPositionRequests().stream()
//                    .map(profileTagPositionRequest -> UserPosition.builder()
//                            .type(profileTagPositionRequest.getType())
//                            .typeLevel(profileTagPositionRequest.getTypeLevel())
//                            .user(user)
//                            .build())
//                    .collect(Collectors.toCollection(ArrayList::new));
//            userPositionRepository.saveAll(userPositions);
//        }
    }

    private void updateUserTags(ProfileRequest request, User user) {
        List<UserTag> userTags = new ArrayList<>();
        for (ProfileTagPositionRequest requestTagDetail : request.getProfileTagPositions()) {
            RoleType type = requestTagDetail.getType();
            for (String tagName: requestTagDetail.getTags()) {
                Tag tag = tagRepository.findByName(tagName);
                if (tag == null) {
                    tag = Tag.create(tagName);
                }
                userTags.add(UserTag.create(tagName, type, tag, user));
            }
        }
        userTagRepository.saveAll(userTags);
    }
}
