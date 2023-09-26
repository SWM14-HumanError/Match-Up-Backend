package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.ApprovedMemberCount;
import com.example.matchup.matchupbackend.dto.request.teamuser.AcceptFormRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.RecruitFormRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.RefuseFormRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.TeamUserFeedbackRequest;
import com.example.matchup.matchupbackend.dto.response.teamuser.RecruitInfoResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.RefuseReasonResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamApprovedInfoResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamUserCardResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateRecruitEx.DuplicateAcceptTeamUserException;
import com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateRecruitEx.DuplicateTeamRecruitException;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidFeedbackException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.*;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.LeaderOnlyPermitException;
import com.example.matchup.matchupbackend.repository.TeamRefuseRepository;
import com.example.matchup.matchupbackend.repository.feedback.FeedbackRepository;
import com.example.matchup.matchupbackend.repository.TeamPositionRepository;
import com.example.matchup.matchupbackend.repository.TeamRecruitRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.userposition.UserPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamUserService {
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final TeamPositionRepository teamPositionRepository;
    private final TeamRecruitRepository teamRecruitRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final AlertCreateService alertCreateService;
    private final UserPositionRepository userPositionRepository;
    private final TeamRefuseRepository teamRefuseRepository;

    /**
     * 팀 상세 페이지에서 팀원들의 정보를 카드형식으로 반환 (일반 유저, 팀장 분기 처리)
     */
    public List<TeamUserCardResponse> getTeamUserCard(Long userID, Long teamID) {
        if (!isTeamLeader(userID, teamID)) // 일반 사용자의 경우
        {
            List<TeamUser> acceptedTeamUsers = teamUserRepository.findAcceptedTeamUserByTeamID(teamID);
            return acceptedTeamUsers.stream().map(
                    acceptedTeamUser -> TeamUserCardResponse.fromEntity(acceptedTeamUser)
            ).collect(Collectors.toList());
        }
        //팀 리더의 경우
        List<TeamUser> teamUsers = teamUserRepository.findTeamUserJoinUser(teamID);
        if (teamUsers.isEmpty()) {
            throw new TeamUserNotFoundException("팀은 최소 1명 이상입니다 (팀장)");
        }
        return teamUsers.stream().map(
                teamUser -> TeamUserCardResponse.fromEntity(teamUser)
        ).collect(Collectors.toList());
    }

    /**
     * 팀의 현재 팀원 모집 현황을 알려줌 (ex. 백엔드 1/3)
     */
    public TeamApprovedInfoResponse getTeamApprovedMemberInfo(Long teamID) {
        List<TeamPosition> teamPositionList = teamPositionRepository.findTeamPositionListByTeamId(teamID);
        if (teamPositionList.isEmpty()) {
            throw new TeamPositionNotFoundException("팀 or 팀 구성 정보가 없습니다");
        }
        List<ApprovedMemberCount> approvedMemberCountList = new ArrayList<>();
        teamPositionList.stream().forEach(teamPosition -> {
            approvedMemberCountList.add(ApprovedMemberCount.fromEntity(teamPosition));
        });
        Team team = teamRepository.findTeamById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        boolean state = team.numberOfApprovedUser() < numberOfMaxTeamMember(teamPositionList) ? false : true;
        //false = 모집중 , true = 모집완료
        return new TeamApprovedInfoResponse(state, approvedMemberCountList);
    }

    /**
     * 팀에서 총 몇명의 팀원을 모집하는지 찾는 매서드
     */
    public Long numberOfMaxTeamMember(List<TeamPosition> teamPositionList) {
        Long max = 0L;
        for (TeamPosition teamPosition : teamPositionList) {
            max += teamPosition.getMaxCount();
        }
        return max;
    }

    /**
     * 팀원으로 유저가 지원
     */
    @Transactional
    public Long recruitToTeam(Long userID, Long teamID, RecruitFormRequest recruitForm) {
        Team team = teamRepository.findTeamById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        User user = userRepository.findUserById(userID).orElseThrow(() -> {
            throw new UserNotFoundException("teamID: " + teamID.toString() + "로 신청한 " +
                    "userID: " + userID.toString() + " 유저 정보를 찾을수 없습니다");
        });
        TeamPosition teamPosition = teamPositionRepository.findTeamPositionByTeamIdAndRole(teamID, recruitForm.getRole())
                .orElseThrow(() -> {
                    throw new TeamPositionNotFoundException("팀 구성 정보가 없습니다");
                });
        if (!isRecruitAvailable(userID, teamID)) {
            throw new DuplicateTeamRecruitException(userID, teamID);
        }
        //성공 로직
        TeamRecruit save = teamRecruitRepository.save(TeamRecruit.of(recruitForm, user, team));
        //알림 저장 로직
        User teamLeader = userRepository.findById(team.getLeaderID()).orElseThrow(() ->
                new UserNotFoundException("팀장 정보를 찾을수 없습니다"));
        alertCreateService.saveTeamUserRecruitAlert(teamLeader, user, team, save.getId());
        return teamUserRepository.save(TeamUser.of(recruitForm, teamPosition, team, user)).getId();
    }

    /**
     * 유저가 팀에 중복해서 지원했는지 확인하는 메서드
     */
    public boolean isRecruitAvailable(Long userID, Long teamID) {
        List<TeamUser> recruitDuplicated = teamUserRepository.isUserRecruitDuplicated(userID, teamID);
        if (recruitDuplicated.size() != 0) return false;
        return true;
    }

    /**
     * 팀장이 유저를 팀원으로 승인함
     */
    @Transactional
    public void acceptUserToTeam(Long leaderID, Long teamID, AcceptFormRequest acceptForm) {
        if (!isTeamLeader(leaderID, teamID)) { // 일반 사용자인 경우
            throw new LeaderOnlyPermitException("팀원으로 유저 수락 부분");
        }
        TeamUser recruitUser = teamUserRepository.findTeamUserJoinTeamAndUser(teamID, acceptForm.getRecruitUserID())
                .orElseThrow(() -> {
                    throw new UserNotFoundException("유저로 지원한 유저 정보가 없습니다");
                });
        if (recruitUser.getApprove()) { // 이미 팀에 속한 팀원인 경우
            throw new DuplicateAcceptTeamUserException(acceptForm.getRecruitUserID(), teamID);
        }
        recruitUser.approveUser();

        teamUserRepository.updateTeamUserStatusByAcceptUser(teamID, acceptForm.getRole());
        log.info("teamUser 업데이트 완료");

        teamPositionRepository.updateTeamPositionStatusByAcceptUser(teamID, acceptForm.getRole());
        log.info("teamPosition 업데이트 완료");

        //알림 저장 로직
        List<User> sendTo = teamUserRepository.findAcceptedTeamUserJoinUser(teamID)
                .stream().map(teamUser -> teamUser.getUser()).collect(Collectors.toList());
        alertCreateService.saveUserAcceptedToTeamAlert(sendTo, recruitUser, acceptForm);
    }

    /**
     * 팀장이 유저를 팀원으로 거절함
     */
    @Transactional
    public void refuseUserToTeam(Long leaderID, Long teamID, RefuseFormRequest refuseForm) {
        if (!isTeamLeader(leaderID, teamID)) { // 일반 사용자인 경우
            throw new LeaderOnlyPermitException("팀원으로 유저 거절 부분");
        }
        // 거절 로직
        TeamUser teamUser = teamUserRepository.findTeamUserJoinTeamAndUser(teamID, refuseForm.getRecruitUserID()).orElseThrow(() -> {
            throw new UserNotFoundException("유저로 지원했던 유저 정보가 없습니다");
        });
        TeamRefuse teamRefuse = teamRefuseRepository.save(TeamRefuse.of(refuseForm, teamUser));
        teamUserRepository.delete(teamUser);
        log.info("userID: " + refuseForm.getRecruitUserID().toString() + " 거절 완료");

        //알림 저장 로직
        TeamUser leader = teamUserRepository.findTeamUserJoinTeamAndUser(teamID, leaderID)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("팀장 정보가 없습니다");
                });
        User recruitUser = userRepository.findById(refuseForm.getRecruitUserID()).orElseThrow(() -> {
            throw new UserNotFoundException("유저로 지원했던 유저 정보가 없습니다");
        });
        alertCreateService.saveUserRefusedToTeamAlert(leader, recruitUser);
    }

    @Transactional
    public void kickUserToTeam(Long leaderID, Long teamID, AcceptFormRequest acceptForm) {
        if (!isTeamLeader(leaderID, teamID)) {
            throw new LeaderOnlyPermitException("유저 강퇴 부분");
        }
        TeamUser kickedUser = teamUserRepository.findTeamUserJoinTeamAndUser(teamID, acceptForm.getRecruitUserID()).orElseThrow(
                () -> {
                    throw new UserNotFoundException("이미 탈퇴되어 유저를 찾을수 없습니다");
                });

        teamUserRepository.updateTeamUserStatusByKickedUser(teamID, acceptForm.getRole());
        log.info("teamUser 업데이트 완료");

        teamPositionRepository.updateTeamPositionStatusByKickedUser(teamID, acceptForm.getRole());
        log.info("teamPosition 업데이트 완료");

        // 알림 저장 로직
        alertCreateService.saveUserKickedToTeamAlert(kickedUser);

        teamUserRepository.deleteTeamUserByTeamIdAndUserId(teamID, acceptForm.getRecruitUserID());
        log.info("userID:" + acceptForm.getRecruitUserID().toString() + " 강퇴 완료");
    }

    public boolean isTeamLeader(Long leaderID, Long teamID) {
        Team team = teamRepository.findTeamById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        if (team.getLeaderID() != leaderID) return false;
        return true;
    }

    /**
     * 팀 구성원끼리 주는 피드백을 생성하는 매서드
     * @param giverID
     * @param teamID
     * @param feedbackRequest
     */
    @Transactional
    public void feedbackToTeamUser(Long giverID, Long teamID, TeamUserFeedbackRequest feedbackRequest) {
        User giver = userRepository.findById(giverID)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 피드백 주는 유저"));
        User receiver = userRepository.findById(feedbackRequest.getReceiverID())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 피드백 받는 유저"));
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀"));
        if (team.getIsDeleted() == 1L) {
            throw new InvalidFeedbackException("teamID: " + teamID, "이미 종료된 팀에는 유저에게 피드백을 보낼 수 없습니다");
        }
        List<TeamUser> twoUserByTeamIdAndUserId = teamUserRepository.findTwoUserByTeamIdAndUserIds(giverID, feedbackRequest.getReceiverID(), teamID);// 같은 팀에 속한 유저인지 검증
        if (twoUserByTeamIdAndUserId.size() != 2) { // 같은 팀에 속한 유저끼리 한 피드백인지 검증
            throw new InvalidFeedbackException("giverID: " + giverID + "  receiverID: " + feedbackRequest.getReceiverID(), "같은 팀끼리만 피드백을 보낼수 있습니다.");
        }
        isPossibleFeedback(giverID, feedbackRequest.getReceiverID(), teamID); // 검증
        Feedback feedback = Feedback.fromDTO(feedbackRequest);
        feedback.setRelation(giver, receiver, team);
        receiver.addFeedback(feedback);
        feedbackRepository.save(feedback);
        alertCreateService.saveFeedbackAlert(giver, receiver, team);
    }

    /**
     * 팀 구성원끼리 피드백을 생성할 수 있는지 검증하는 매서드
     * 1. 프로젝트가 끝났는데 리뷰를 한번 더 보내는지
     * 2. 아직 리뷰 보낼 시간이 아닌데 리뷰를 한번 더 보내는지
     */
    public void isPossibleFeedback(Long giverID, Long receiverID, Long teamID) { //todo valid 테스트
        List<Feedback> feedbacksByUserAndTeam = feedbackRepository.findFeedbackByUserAndTeam(giverID, receiverID, teamID);
        if (!feedbacksByUserAndTeam.isEmpty()) { //피드백이 있으면 리뷰 보낼 시간이 됐는지 확인
            Feedback feedback = feedbacksByUserAndTeam.get(0);
            Duration duration = Duration.between(feedback.getCreateTime(), LocalDateTime.now());
            if (duration.toDays() < 7) {
                throw new InvalidFeedbackException(feedback.getCreateTime().toString(), "피드백은 7일에 한번만 보낼 수 있습니다");
            }
        }
        if (giverID == receiverID) {
            throw new InvalidFeedbackException("giverID: " + giverID.toString(), "자기 자신에게 피드백을 보낼 수 없습니다");
        }
    }

    /**
     * 유저 지원서 모달창 정보를 반환하는 매서드
     * @param userID
     * @param teamID
     * @param recruitID
     * @return
     */
    public RecruitInfoResponse getRecruitInfo(Long userID, Long teamID, Long recruitID) {
        // 팀장인지 검증
        if (!isTeamLeader(userID, teamID)) {
            throw new LeaderOnlyPermitException("지원서 열람은 팀장만 가능합니다.");
        }
        TeamRecruit teamRecruit = teamRecruitRepository.findRecruitJoinUserById(recruitID)
                .orElseThrow(() -> {
                    throw new RecruitNotFoundException("지원서 열람 중 지원서를 찾을수 없습니다.");
                });
        List<UserPosition> userPosition = userPositionRepository.findByUserId(teamRecruit.getUser().getId());
        return RecruitInfoResponse.from(teamRecruit, userPosition);
    }

    /**
     * 팀원 거절 사유를 반환하는 매서드
     * @param userID
     * @param refuseID
     * @return
     */
    public RefuseReasonResponse getUserRefuseReason(Long userID, Long refuseID) {
        TeamRefuse teamRefuse = teamRefuseRepository.findRefuseInfoJoinUserAndTeamById(refuseID)
                .orElseThrow(() -> {
                    throw new RefuseInfoNotFoundException("팀원 거절 정보가 존재하지 않습니다.");
                });
        User leader = userRepository.findById(teamRefuse.getTeam().getLeaderID())
                .orElseThrow(() -> {
                    throw new UserNotFoundException("팀장 정보를 찾을수 없습니다.");
                });
        return RefuseReasonResponse.of(teamRefuse, leader);
    }
}