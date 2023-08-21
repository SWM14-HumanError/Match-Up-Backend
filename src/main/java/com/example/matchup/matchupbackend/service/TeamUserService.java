package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.ApprovedMember;
import com.example.matchup.matchupbackend.dto.TeamApprovedInfo;
import com.example.matchup.matchupbackend.dto.TeamUserCardResponse;
import com.example.matchup.matchupbackend.dto.teamuser.AcceptForm;
import com.example.matchup.matchupbackend.dto.teamuser.RecruitForm;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.repository.TeamPositionRepository;
import com.example.matchup.matchupbackend.repository.TeamRecruitRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

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
     * 팀 상세 페이지에서 팀원들의 정보를 카드형식으로 반환
     */
    public List<TeamUserCardResponse> getTeamUserCard(Long teamID) {
        List<TeamUser> allByTeam = teamUserRepository.findAllByTeamID(teamID);
        return allByTeam.stream().map(
                teamUser -> {
                    User user = teamUser.getUser();
                    return TeamUserCardResponse.builder()
                            .userID(user.getId())
                            .profileImageURL(user.getPictureUrl())
                            .memberLevel(user.getUserLevel())
                            .nickname(user.getName())
                            .position(makePosition(user.getPosition(), user.getUserLevel()))
                            .score(user.getReviewScore())
                            .like(user.getLikes())
                            .techStacks(user.returnStackList())
                            .role((teamUser.getRole()))
                            .approve(teamUser.getApprove())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    public static Position makePosition(String positionName, Long positionLevel) {
        return new Position(positionName, positionLevel);
    }

    /**
     * 팀의 현재 팀원 모집 현황을 알려줌 (ex. 백엔드 1/3)
     */
    public TeamApprovedInfo getTeamRecruitInfo(Long teamID) {
        List<TeamPosition> teamPositionList = teamPositionRepository.findTeamPositionListByTeamId(teamID);
        List<ApprovedMember> approvedMemberList = new ArrayList<>();
        teamPositionList.stream().forEach(teamPosition -> {
            ApprovedMember approvedMember = ApprovedMember.builder()
                    .role(teamPosition.getRole())
                    .stacks(teamPosition.stringTagList())
                    .maxCount(teamPosition.getMaxCount())
                    .count(teamPosition.getTeam().numberOfUserByPosition(teamPosition.getRole()))
                    .build();
            approvedMemberList.add(approvedMember);
        });
        boolean state =
                teamRepository.findTeamById(teamID).numberOfApprovedUser() < numberOfMaxTeamMember(teamPositionList) ? false : true;
        //false = 모집중 , true = 모집완료 + try-catch 예외처리
        return new TeamApprovedInfo(state, approvedMemberList);
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
        Team team = teamRepository.findTeamById(teamID);
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
        Team team = teamRepository.findTeamById(teamID);
        if (!isTeamLeader(leaderID, team)) {
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

    public boolean isTeamLeader(Long leaderID, Team team) {
        if (team.getLeaderID() != leaderID) return false;
        return true;
    }
}