package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.*;
import com.example.matchup.matchupbackend.dto.mentoring.MentoringCardResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.entity.TeamPosition;
import com.example.matchup.matchupbackend.repository.TeamPositionRepository;
import com.example.matchup.matchupbackend.repository.tag.TagRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamtag.TeamTagRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamTagRepository teamTagRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final TeamPositionRepository teamPositionRepository;

    public SliceTeamResponse searchSliceTeamResponseList(TeamSearchRequest teamSearchRequest, Pageable pageable) {
        Slice<Team> teamSliceByTeamRequest = teamRepository.findTeamSliceByTeamRequest(teamSearchRequest, pageable);
        SliceTeamResponse sliceTeamResponse = new SliceTeamResponse(teamSearchResponseList(teamSliceByTeamRequest.getContent()), teamSliceByTeamRequest.getSize(), teamSliceByTeamRequest.hasNext());
        return sliceTeamResponse;
    }

    public List<TeamSearchResponse> teamSearchResponseList(List<Team> teamList) {
        List<TeamSearchResponse> teamSearchResponseList = new ArrayList<>();
        Map<Long, User> userMap = getUserMap();
        teamList.stream()
                .forEach(team -> {
                    TeamSearchResponse teamSearchResponse = TeamSearchResponse
                            .builder()
                            .id(team.getId())
                            .title(team.getTitle())
                            .description(team.getDescription())
                            .like(team.getLike())
                            .thumbnailUrl(team.getThumbnailUrl())
                            .techStacks(team.returnStackList())
                            .leaderID(team.getLeaderID())
                            .leaderName(userMap.get(team.getLeaderID()).getName())
                            .leaderLevel(userMap.get(team.getLeaderID()).getUserLevel())
                            .build();
                    teamSearchResponseList.add(teamSearchResponse);
                });
        return teamSearchResponseList;
    }

    public Map<Long, User> getUserMap() {
        Map<Long, User> userMap = new HashMap<>();
        userRepository.findAllUser().stream().forEach(user -> userMap.put(user.getId(), user));
        return userMap;
    }

    @Transactional
    public Long makeNewTeam(Long leaderID,TeamCreateRequest teamCreateRequest) {
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
                .leaderID(leaderID)
                .build();

        makeTeamPosition(teamCreateRequest, team);

        TeamUser teamUser = TeamUser.builder()
                .user(userRepository.findById(leaderID).orElse(null))
                .team(team)
                .approve(true)
                .build();

        return teamUserRepository.save(teamUser).getId();
    }

    @Transactional
    public void makeTeamPosition(TeamCreateRequest teamCreateRequest, Team team) {
        List<TeamPosition> teamPositions = new ArrayList<>();
        teamCreateRequest.getMemberList().stream().forEach(member -> {
            TeamPosition teamPosition = TeamPosition.builder()
                    .role(member.getRole())
                    .maxCount(member.getMaxCount())
                    .build();
            teamPosition.addTeam(team);

            teamPositions.add(teamPosition);
            teamPositionRepository.save(teamPosition);
        });
        makeTeamPositionTag(teamPositions, teamCreateRequest);
    }

    @Transactional
    public void makeTeamPositionTag(List<TeamPosition> teamPositions, TeamCreateRequest teamCreateRequest) {
        for (TeamPosition teamPosition : teamPositions) {
            List<String> tagList = teamCreateRequest.returnTagListByRole(teamPosition.getRole());
            tagList.stream().forEach(tagName -> {

                Tag isExistTag = tagRepository.findByName(tagName);
                if (isExistTag != null) //이미 있는 태그
                {
                    TeamTag teamTag = TeamTag.builder()
                            .teamPosition(teamPosition)
                            .tagName(tagName)
                            .team(teamPosition.getTeam())
                            .tag(isExistTag)
                            .build();
                    teamTagRepository.save(teamTag);
                } else {
                    Tag newTag = Tag.builder().name(tagName).build();
                    tagRepository.save(newTag);
                    TeamTag teamTag = TeamTag.builder()
                            .teamPosition(teamPosition)
                            .tagName(tagName)
                            .team(teamPosition.getTeam())
                            .tag(newTag)
                            .build();
                    teamTagRepository.save(teamTag);
                }

            });
        }
    }

    @Transactional
    public Long updateTeam(Long leaderID, Long teamID, TeamCreateRequest teamCreateRequest) {
        Team team = teamRepository.findById(teamID).orElse(null);
        if(leaderID != team.getLeaderID()) {
            throw new IllegalArgumentException("리더만 팀 정보를 변경 할 수 있습니다");
        }
        return team.updateTeam(teamCreateRequest);
    }

    public boolean isUpdatable(Long teamID, TeamCreateRequest teamCreateRequest) {
        Team team = teamRepository.findById(teamID).orElse(null);
        if (team == null || team.getIsDeleted() == 1L) return false;
        for (TeamUser teamUser : team.getTeamUserList()) {
            for (Member member : teamCreateRequest.getMemberList()) {
                if (teamUser.getRole() == member.getRole()
                        && teamUser.getCount() > member.getMaxCount()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Transactional
    public void deleteTeam(Long leaderID, Long teamID) {
        Team team = teamRepository.findById(teamID).orElse(null);
        if(leaderID != team.getLeaderID()) {
            throw new IllegalArgumentException("리더만 팀을 삭제 할 수 있습니다");
        }
        log.info("deleted team ID : " + team.deleteTeam().toString());
    }

    public TeamDetailResponse getTeamInfo(Long teamID) {
        return teamRepository.findTeamInfoByTeamId(teamID);
    }

    public MeetingSpot getTeamMeetingSpot(Long teamID) {
        return teamRepository.findMeetingSpotByTeamId(teamID);
    }

    public List<MentoringCardResponse> getTeamMentoringCardList(Long teamID) {
        List<TeamMentoring> teamMentoringList = teamRepository.findTeamMentoringListByTeamId(teamID);
        List<MentoringCardResponse> teamMentoringCards = new ArrayList<>();
        teamMentoringList.stream().forEach(
                teamMentoring -> {
                    Mentoring mentoring = teamMentoring.getMentoring();
                    teamMentoringCards.add(MentoringCardResponse.builder()
                            .mentoringID(mentoring.getId())
                            .thumbnailURL(mentoring.getThumbnailURL())
                            .title(mentoring.getTitle())
                            .positionName(mentoring.getMentor().getPosition())
                            .positionLevel(mentoring.getMentor().getPositionLevel())
                            .mentorProfileURL(mentoring.getMentor().getPictureUrl())
                            .mentorNickname(mentoring.getMentor().getName())
                            .score(mentoring.returnMentoringReviewAverage())
                            .like(mentoring.getLikes())
                            .build()
                    );
                }
        );
        return teamMentoringCards;
    }

    public List<String> getTeamTagStringList(Long teamID) {
        List<TeamTag> teamTags = teamRepository.findTeamTagByTeamId(teamID);
        List<String> teamTagNames = new ArrayList<>();
        teamTags.stream().forEach(teamTag -> {
            teamTagNames.add(teamTag.getTag().getName());
        });
        return teamTagNames;
    }

    public TeamType getTeamType(Long teamID) {
        Team teamById = teamRepository.findTeamById(teamID);
        TeamType teamType = TeamType.builder()
                .teamType(teamById.getType())
                .detailType(teamById.getDetailType())
                .build();
        return teamType;
    }
}

