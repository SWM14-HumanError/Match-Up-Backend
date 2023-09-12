package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.ApprovedMemberCount;
import com.example.matchup.matchupbackend.dto.request.teamuser.TeamUserFeedbackRequest;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamApprovedInfoResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamUserCardResponse;
import com.example.matchup.matchupbackend.dto.request.teamuser.AcceptFormRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.RecruitFormRequest;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx.DuplicateAcceptTeamUserException;
import com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx.DuplicateTeamRecruitException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamPositionNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamUserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.LeaderOnlyPermitException;
import com.example.matchup.matchupbackend.repository.FeedbackRepository;
import com.example.matchup.matchupbackend.repository.TeamPositionRepository;
import com.example.matchup.matchupbackend.repository.TeamRecruitRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<TeamUser> teamUsers = teamUserRepository.findAllTeamUserByTeamID(teamID);
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
        teamRecruitRepository.save(TeamRecruit.of(recruitForm, user, team));
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
        TeamUser recruitUser = teamUserRepository.findTeamUserByTeamIdAndUserId(teamID, acceptForm.getRecruitUserID())
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
    }

    /**
     * 팀장이 유저를 팀원으로 거절함
     */
    @Transactional
    public void refuseUserToTeam(Long leaderID, Long teamID, AcceptFormRequest acceptForm) {
        if (!isTeamLeader(leaderID, teamID)) { // 일반 사용자인 경우
            throw new LeaderOnlyPermitException("팀원으로 유저 거절 부분");
        }
        teamUserRepository.deleteTeamUserByTeamIdAndUserId(teamID, acceptForm.getRecruitUserID());
        log.info("userID: " + acceptForm.getRecruitUserID().toString() + " 거절 완료");
    }

    @Transactional
    public void kickUserToTeam(Long leaderID, Long teamID, AcceptFormRequest acceptForm) {
        if (!isTeamLeader(leaderID, teamID)) {
            throw new LeaderOnlyPermitException("유저 강퇴 부분");
        }
        teamUserRepository.findTeamUserByTeamIdAndUserId(teamID, acceptForm.getRecruitUserID()).orElseThrow(
                () -> {
                    throw new UserNotFoundException("이미 탈퇴되어 유저를 찾을수 없습니다");
                });

        teamUserRepository.updateTeamUserStatusByKickedUser(teamID, acceptForm.getRole());
        log.info("teamUser 업데이트 완료");

        teamPositionRepository.updateTeamPositionStatusByKickedUser(teamID, acceptForm.getRole());
        log.info("teamPosition 업데이트 완료");

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

    @Transactional
    public void feedbackToTeamUser(Long giverID, Long teamID, TeamUserFeedbackRequest feedbackRequest) {
        User giverUser = userRepository.findById(giverID)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저"));
        User receiverUser = userRepository.findById(feedbackRequest.getReceiverID())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저"));
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀"));

        Feedback feedback = Feedback.fromDTO(feedbackRequest);
        feedback.setRelation(giverUser, receiverUser, team);
        receiverUser.addReview(feedback);
        feedbackRepository.save(feedback);
    }
}