package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.*;
import com.example.matchup.matchupbackend.dto.mentoring.MentoringCardResponse;
import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.request.team.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.response.team.MeetingSpotResponse;
import com.example.matchup.matchupbackend.dto.response.team.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamDetailResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamTypeResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.entity.TeamPosition;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidMemberValueException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamDetailNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.LeaderOnlyPermitException;
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
@Transactional(readOnly = true)
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
                    teamSearchResponseList.add(TeamSearchResponse.from(team, userMap));
                });
        return teamSearchResponseList;
    }

    /**
     * 모든 유저 정보를 MAP으로 만들어주는 매서드
     * key: userID, value: user
     */
    public Map<Long, User> getUserMap() {
        Map<Long, User> userMap = new HashMap<>();
        userRepository.findAllUser().stream().forEach(user -> userMap.put(user.getId(), user));
        return userMap;
    }

    /**
     * 새로운 팀 생성
     */
    @Transactional
    public Long makeNewTeam(Long leaderID, TeamCreateRequest teamCreateRequest) {
        makeTeamPosition(teamCreateRequest, Team.of(leaderID, teamCreateRequest));
        User user = userRepository.findById(leaderID).orElseThrow(() -> {
            throw new UserNotFoundException("팀을 만든 유저를 찾을수 없습니다");
        });
        return teamUserRepository.save(TeamUser.of("Leader", 1L, true, 1L, Team.of(leaderID, teamCreateRequest), user)).getId();
    }

    /**
     * 새로운 팀 생성 / 직무별 팀원 모집 정보 생성
     */
    @Transactional
    public void makeTeamPosition(TeamCreateRequest teamCreateRequest, Team team) {
        List<TeamPosition> teamPositions = new ArrayList<>();
        teamCreateRequest.getMemberList().stream().forEach(member -> {
            TeamPosition teamPosition = TeamPosition.of(member.getRole(), 0L, member.getMaxCount(), team);
            teamPositions.add(teamPosition);
            teamPositionRepository.save(teamPosition);
        });
        makeTeamPositionTag(teamPositions, teamCreateRequest);
    }

    /**
     * 새로운 팀 생성 / 직무별 모집 정보 기반으로 "팀의 기술 스택 종합" 해서 생성
     */
    @Transactional
    public void makeTeamPositionTag(List<TeamPosition> teamPositions, TeamCreateRequest teamCreateRequest) {
        for (TeamPosition teamPosition : teamPositions) {
            List<String> tagList = teamCreateRequest.returnTagListByRole(teamPosition.getRole());
            tagList.stream().forEach(tagName -> {
                Tag isExistTag = tagRepository.findByName(tagName);
                if (isExistTag != null) //이미 있는 태그
                {
                    teamTagRepository.save(TeamTag.of(tagName, teamPosition, teamPosition.getTeam(), isExistTag));
                } else { //새로운 태그
                    Tag newTag = Tag.builder().name(tagName).build();
                    tagRepository.save(newTag);
                    teamTagRepository.save(TeamTag.of(tagName, teamPosition, teamPosition.getTeam(), newTag));
                }
            });
        }
    }

    /**
     * 팀장이 팀 정보 업데이트
     */
    @Transactional
    public Long updateTeam(Long leaderID, Long teamID, TeamCreateRequest teamCreateRequest) {
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        isUpdatableTeam(leaderID, team, teamCreateRequest);
        log.info("Update team ID : " + team.deleteTeam().toString());
        return team.updateTeam(teamCreateRequest);
    }

    /**
     * 팀 수정 할때 현재 있는 팀원보다 더 적은 max 팀원을 설정 할 경우 예외
     */
    private void isUpdatableTeam(Long leaderID, Team team, TeamCreateRequest teamCreateRequest) {
        if (leaderID != team.getLeaderID()) { // 팀 수정 시도 하는 사람이 리더인지 체크
            throw new LeaderOnlyPermitException("팀 업데이트 - teamID: " + team.getId());
        }
        if (team.getIsDeleted() == 1L) { // 이미 지워진 팀을 수정 하는지 체크
            throw new TeamNotFoundException("삭제 된 게시물");
        }

        for (TeamUser teamUser : team.getTeamUserList()) { // 현재 팀원보다 max 인원을 적게 설정했는지 체크
            for (Member member : teamCreateRequest.getMemberList()) {
                if (teamUser.getRole().equals(member.getRole())
                        && teamUser.getCount() > member.getMaxCount()) {
                    throw new InvalidMemberValueException(member.getMaxCount());
                }
            }
        }
    }

    @Transactional
    public void deleteTeam(Long leaderID, Long teamID) {
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        if (!leaderID.equals(team.getLeaderID())) {
            throw new LeaderOnlyPermitException("팀 삭제 - teamID: " + teamID);
        }
        log.info("deleted team ID : " + team.deleteTeam().toString());
    }

    public TeamDetailResponse getTeamInfo(Long teamID) {
        TeamDetailResponse teamInfoByTeamId = teamRepository.findTeamInfoByTeamId(teamID);
        if (teamInfoByTeamId == null) {
            throw new TeamDetailNotFoundException("팀 상세 정보가 없습니다");
        }
        return teamInfoByTeamId;
    }

    public MeetingSpotResponse getTeamMeetingSpot(Long teamID) {
        MeetingSpotResponse meetingSpotByTeamId = teamRepository.findMeetingSpotByTeamId(teamID);
        if (meetingSpotByTeamId == null) {
            throw new TeamDetailNotFoundException("팀 회의 장소가 없습니다");
        }
        return meetingSpotByTeamId;
    }

    public List<MentoringCardResponse> getTeamMentoringCardList(Long teamID) {
        List<TeamMentoring> teamMentoringList = teamRepository.findTeamMentoringListByTeamId(teamID);
        List<MentoringCardResponse> teamMentoringCards = new ArrayList<>();
        teamMentoringList.stream().forEach(
                teamMentoring -> {
                    Mentoring mentoring = teamMentoring.getMentoring();
                    teamMentoringCards.add(MentoringCardResponse.fromEntity(mentoring));
                });
        return teamMentoringCards;
    }

    public List<String> getTeamTagStringList(Long teamID) {
        List<TeamTag> teamTags = teamRepository.findTeamTagByTeamId(teamID);
        if (teamTags.isEmpty()) {
            throw new TeamDetailNotFoundException("팀 태그가 없습니다");
        }
        List<String> teamTagNames = new ArrayList<>();
        teamTags.stream().forEach(teamTag -> {
            teamTagNames.add(teamTag.getTag().getName());
        });
        return teamTagNames;
    }

    public TeamTypeResponse getTeamType(Long teamID) {
        Team teamById = teamRepository.findTeamById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        TeamTypeResponse teamType = TeamTypeResponse.fromTeamEntity(teamById);
        return teamType;
    }
}

