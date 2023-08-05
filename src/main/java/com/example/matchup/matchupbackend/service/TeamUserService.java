package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.TeamUserCardResponse;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.TeamUser;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamUserService {
    private final TeamUserRepository teamUserRepository;
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
                            .positionName(user.getPosition())
                            .positionLevel(user.getPositionLevel())
                            .score(user.getReviewScore())
                            .like(user.getLikes())
                            .stacks(user.returnTagList())
                            .role((teamUser.getRole()))
                            .approve(teamUser.getApprove())
                            .build();
                }
        ).collect(Collectors.toList());
    }
}
