package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.ApprovedMember;
import com.example.matchup.matchupbackend.dto.TeamApprovedInfo;
import com.example.matchup.matchupbackend.dto.TeamUserCardResponse;
import com.example.matchup.matchupbackend.entity.TeamPosition;
import com.example.matchup.matchupbackend.entity.TeamUser;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamUserService {
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    public List<TeamUserCardResponse> getTeamUserCard(Long teamID)
    {
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
    public static Position makePosition(String positionName, String positionLevel){
        return new Position(positionName, positionLevel);
    }
    public TeamApprovedInfo getTeamRecruitInfo(Long teamID)
    {
        List<TeamPosition> teamPositionList = teamRepository.findTeamPositionListByTeamId(teamID);
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
    public Long numberOfMaxTeamMember(List<TeamPosition> teamPositionList)
    {
        Long max = 0L;
        for(TeamPosition teamPosition : teamPositionList)
        {
            max+=teamPosition.getMaxCount();
        }
        return max;
    }
}
