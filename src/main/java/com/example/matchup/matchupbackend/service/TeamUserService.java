package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.ApprovedMemberCount;
import com.example.matchup.matchupbackend.dto.TeamApprovedInfoResponse;
import com.example.matchup.matchupbackend.dto.response.teamuser.TeamUserCardResponse;
import com.example.matchup.matchupbackend.dto.teamuser.AcceptForm;
import com.example.matchup.matchupbackend.dto.teamuser.RecruitForm;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamPositionNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamUserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.LeaderOnlyPermitException;
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
public class TeamUserService {
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final TeamPositionRepository teamPositionRepository;
    private final TeamRecruitRepository teamRecruitRepository;
    private final UserRepository userRepository;

    /**
     * 팀 상세 페이지에서 팀원들의 정보를 카드형식으로 반환 - 팀장 mode
     */
    public List<TeamUserCardResponse> getTeamUserCard(Long userID,Long teamID) {
        if(!isTeamLeader(userID, teamID))
        {
            throw new LeaderOnlyPermitException();
        }
        List<TeamUser> allByTeam = teamUserRepository.findAllTeamUserByTeamID(teamID);
        if (allByTeam.isEmpty()) {
            throw new TeamUserNotFoundException("팀은 최소 1명 이상입니다 (팀장)");
        }
        return allByTeam.stream().map(
                teamUser -> TeamUserCardResponse.fromEntity(teamUser)
        ).collect(Collectors.toList());
    }

    /**
     * 팀의 현재 팀원 모집 현황을 알려줌 (ex. 백엔드 1/3)
     */
    public TeamApprovedInfoResponse getTeamApprovedMemberInfo(Long teamID) {
        List<TeamPosition> teamPositionList = teamPositionRepository.findTeamPositionListByTeamId(teamID);
        if (teamPositionList.isEmpty()) {
            throw new TeamPositionNotFoundException();
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
    public Long recruitToTeam(Long userID, Long teamID, RecruitForm recruitForm) {
        Team team = teamRepository.findTeamById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        User user = userRepository.findUserById(userID);
        TeamPosition teamPosition = teamPositionRepository.findTeamPositionByTeamIdAndRole(teamID, recruitForm.getRole());
        if (!isRecruitAvailable(userID, teamID)) {
            throw new RuntimeException("니 같은팀에 또 신청함");
        }

        TeamRecruit teamRecruit = TeamRecruit.builder()
                .content(recruitForm.getContent())
                .role(recruitForm.getRole())
                .user(user)
                .team(team)
                .build();
        teamRecruitRepository.save(teamRecruit);

        TeamUser teamUser = TeamUser.builder()
                .role(recruitForm.getRole())
                .approve(false)
                .count(teamPosition.getCount())
                .maxCount(teamPosition.getMaxCount())
                .team(team)
                .user(user)
                .build();
        return teamUserRepository.save(teamUser).getId();
    }

    public boolean isRecruitAvailable(Long userID, Long teamID) {
        List<TeamUser> recruitDuplicated = teamUserRepository.isUserRecruitDuplicated(userID, teamID);
        if (recruitDuplicated.size() != 0) return false;
        return true;
    }

    /**
     * 팀장이 유저를 팀원으로 승인함
     */
    @Transactional
    public void acceptUserToTeam(Long leaderID, Long teamID, AcceptForm acceptForm) {
        if (!isTeamLeader(leaderID, teamID)) {
            throw new RuntimeException("팀장 아니면 팀원으로 수락 못함");
        }
        TeamUser recruitUser = teamUserRepository.findTeamUserByTeamIdAndUserId(teamID, acceptForm.getRecruitUserID());
        if (recruitUser.getApprove()) {
            throw new RuntimeException("얘는 이미 우리 팀원인데?");
        }
        recruitUser.approveUser();

        teamUserRepository.updateTeamUserStatusByAcceptUser(teamID, acceptForm.getRole());
        log.info("teamUser 업데이트 완료");

        teamPositionRepository.updateTeamPositionStatusByAcceptUser(teamID, acceptForm.getRole());
        log.info("teamPosition 업데이트 완료");
    }

    @Transactional
    public void refuseUserToTeam(Long leaderID, Long teamID, AcceptForm acceptForm) {
        if (!isTeamLeader(leaderID, teamID)) {
            throw new RuntimeException("팀장 아니면 팀원으로 수락 못함");
        }
        teamUserRepository.deleteTeamUserByTeamIdAndUserId(teamID, acceptForm.getRecruitUserID());
        log.info("userID: " + acceptForm.getRecruitUserID().toString() + " 거절 완료");
    }

    @Transactional
    public void kickUserToTeam(Long leaderID, Long teamID, AcceptForm acceptForm) {
        if (!isTeamLeader(leaderID, teamID)) {
            throw new RuntimeException("팀장 아니면 팀원 강퇴 못함");
        }
        try {
            teamUserRepository.findTeamUserByTeamIdAndUserId(teamID, acceptForm.getRecruitUserID());
        } catch (NullPointerException exception) {
            log.info("이미 강퇴된 유저 입니다");
            return;
        }
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
}