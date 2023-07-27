package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.*;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.repository.tag.TagRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamtag.TeamTagRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamTagRepository teamTagRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public SliceTeamResponse SearchSliceTeamList(TeamSearchRequest teamSearchRequest, Pageable pageable) {
        Slice<TeamSearchResponse> teamSliceByTeamRequest = teamRepository.findTeamSliceByTeamRequest(teamSearchRequest, pageable);
        SliceTeamResponse sliceTeamResponse = new SliceTeamResponse(teamSliceByTeamRequest.getContent(), teamSliceByTeamRequest.getSize(), teamSliceByTeamRequest.hasNext());
        return sliceTeamResponse;
    }

    @Transactional
    public Long makeNewTeam(TeamCreateRequest teamCreateRequest) {
        // String 으로 반환된 태그를 Tag 객체 리스트로 만듬
        Team team = Team.builder()
                .title(teamCreateRequest.getName())
                .description(teamCreateRequest.getDescription())
                .type(teamCreateRequest.getType().getTeamType())
                .detailType(teamCreateRequest.getType().getDetailType())
                .thumbnailUrl(teamCreateRequest.getThumbnailUrl())
                .like(0L)
                .onOffline(teamCreateRequest.getMeetingSpot().getOnOffline())
                .city(teamCreateRequest.getMeetingSpot().getCity())
                .detailSpot(teamCreateRequest.getMeetingSpot().getDetailSpot())
                .recruitFinish("NF")
                .build();

        makeNewTeamTag(teamCreateRequest, team);

        TeamUser teamUser = TeamUser.builder()
                .user(userRepository.findById(teamCreateRequest.getLeaderID()).orElse(null))
                .team(team)
                .build();

        return teamUserRepository.save(teamUser).getId();
    }

    @Transactional
    public void makeNewTeamTag(TeamCreateRequest teamCreateRequest, Team team) {
        for (String tagName : teamCreateRequest.returnTagList()) {
            Tag isExistTag = tagRepository.findByName(tagName);
            if (isExistTag != null) //이미 있는 태그
            {
                TeamTag teamTag = TeamTag.builder()
                        .team(team)
                        .tag(isExistTag)
                        .build();
                teamTagRepository.save(teamTag);
                team.addTeamTagList(teamTag);
            } else { //처음 생성한 태그
                Tag newTag = Tag.builder().name(tagName).build();
                tagRepository.save(newTag);
                TeamTag newTeamTag = TeamTag.builder()
                        .team(team)
                        .tag(newTag)
                        .build();
                teamTagRepository.save(newTeamTag);
                team.addTeamTagList(newTeamTag);
            }
        }
    }

    @Transactional
    public Long updateTeam(Long teamID, TeamCreateRequest teamCreateRequest)
    {
        Team team = teamRepository.findById(teamID).orElse(null);
        return team.updateTeam(teamCreateRequest);
    }

    public boolean isUpdatable(Long teamID, TeamCreateRequest teamCreateRequest) {
        Team team = teamRepository.findById(teamID).orElse(null);
        if(team == null) return false;
        for (TeamUser teamUser : team.getTeamUserList()) {
            for (Member member : teamCreateRequest.getMemberList()) {
                if (teamUser.getRole() == member.getRole()
                        && teamUser.getCount() > member.getCount()) {
                    return false;
                }
            }
        }
        return true;
    }
}

