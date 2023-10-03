package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Member;
import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.mentoring.MentoringCardResponse;
import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.request.team.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.response.team.*;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateFeedEx.DuplicateLikeException;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidMemberValueException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.*;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.LeaderOnlyPermitException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.LikeRepository;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.matchup.matchupbackend.error.ErrorCode.COMMENT_NOT_FOUND;

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
    private final FileService fileService;
    private final AlertCreateService alertCreateService;
    private final TokenProvider tokenProvider;
    private final LikeRepository likeRepository;

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
        Team team = Team.of(leaderID, teamCreateRequest);
        if (teamCreateRequest.getBase64Thumbnail() != null) { //썸네일 사진이 있는 경우
            MultipartFile multipartFile = fileService.convertBase64ToFile(teamCreateRequest.getBase64Thumbnail());
            UploadFile uploadFile = fileService.storeFile(multipartFile);
            team.setUploadFile(uploadFile);
        }
        teamRepository.save(team);
        makeTeamPosition(teamCreateRequest, team); //팀의 직무별 팀원 모집 정보 생성
        User user = userRepository.findById(leaderID).orElseThrow(() -> {
            throw new UserNotFoundException("팀을 만든 유저를 찾을수 없습니다");
        });
        Long teamID = teamUserRepository.save(TeamUser.of("Leader", 1L, true,  team, user)).getTeam().getId();
        alertCreateService.saveTeamCreateAlert(teamID, user, teamCreateRequest);
        return teamID;
    }

    /**
     * 새로운 팀 생성 / 직무별 팀원 모집 정보 생성
     */
    @Transactional
    public void makeTeamPosition(TeamCreateRequest teamCreateRequest, Team team) {
        List<TeamPosition> teamPositions = new ArrayList<>();
        teamCreateRequest.getMemberList().stream().forEach(member -> {
            TeamPosition teamPosition = TeamPosition.of(member.getRole(), 0L, member.getMaxCount(), team);
            teamPositionRepository.save(teamPosition);
            teamPositions.add(teamPosition);
        });
        makeTeamPositionTag(teamPositions, teamCreateRequest);
    }

    /**
     * 새로운 팀 생성 / 직무별 모집 정보 기반으로 "팀의 기술 스택 종합" 해서 생성
     */
    @Transactional
    public void makeTeamPositionTag(List<TeamPosition> teamPositions, TeamCreateRequest teamCreateRequest) {
        for (TeamPosition teamPosition : teamPositions) {
            List<String> tagList = teamCreateRequest.returnTagListByRole(teamPosition.getRole()); //BE, FE 태그 다 섞여 있음
            tagList.stream().forEach(tagName -> {
                Tag isExistTag = tagRepository.findByName(tagName);
                if (isExistTag != null) //이미 있는 태그
                {
                    teamTagRepository.save(TeamTag.of(tagName, teamPosition, teamPosition.getTeam(), isExistTag));
                } else { //새로운 태그
                    Tag newTag = Tag.builder().name(tagName).build();
                    tagRepository.save(newTag);
                    teamTagRepository.save(TeamTag.of(tagName, teamPosition, teamPosition.getTeam(), newTag)); //todo 팀이 이미 있는데 같은 id로 저장해서 에러 나는듯
                }
            });
        }
    }

    /**
     * 팀장이 팀 정보 업데이트
     */
    @Transactional
    public Long updateTeam(Long leaderID, Long teamID, TeamCreateRequest teamCreateRequest) {
        Team team = teamRepository.findTeamById(teamID).orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");});

        //팀 검증 로직
        isUpdatableTeam(leaderID, team, teamCreateRequest);

        //팀 포지션, 기술 스택 초기화
        List<TeamPosition> teamPositions = teamPositionRepository.findByTeam(team);
        for (int i = teamPositions.size() - 1; i >= 0; i--) {
            if (teamPositions.get(i).getCount() == 0) {
                teamPositionRepository.delete(teamPositions.get(i)); // db에서 삭제
                teamPositions.remove(i); //가져온 list에서 삭제
            }
        }
        teamTagRepository.deleteByTeamId(teamID); // 팀의 기술 스택 전체 삭제

        //썸네일 사진 수정 로직
        if (teamCreateRequest.getBase64Thumbnail() != null) {
            fileService.deleteImage(team.getThumbnailUrl());
            MultipartFile multipartFile = fileService.convertBase64ToFile(teamCreateRequest.getBase64Thumbnail());
            UploadFile uploadFile = fileService.storeFile(multipartFile);
            team.setUploadFile(uploadFile);
        }

        //실제 수정 로직
        team.updateTeam(teamCreateRequest); // 팀의 기본 정보 업데이트
        teamRepository.save(team);
        updateTeamPosition(teamPositions, teamCreateRequest, team); // 팀의 직무별 팀원 모집 정보 업데이트

        //팀 업데이트 알림 보내는 로직
        List<User> sendAlertTarget = teamUserRepository.findTeamUserJoinUser(teamID)
                .stream()
                .map(teamUser -> teamUser.getUser())
                .toList();
        alertCreateService.saveTeamUpdateAlert(teamID, sendAlertTarget, teamCreateRequest);

        log.info("Update team ID : " + teamID);
        return teamID;
    }

    /**
     * 팀의 포지션을 업데이트 하는 매서드
     * @param teamPositionList
     * @param teamCreateRequest
     * @param team
     */
    @Transactional
    public void updateTeamPosition(List<TeamPosition> teamPositionList, TeamCreateRequest teamCreateRequest, Team team) {
        List<TeamPosition> newTeamPositions = new ArrayList<>();
        Map<String, TeamPosition> teamPositionMappedRole = new HashMap<>();
        teamPositionList.stream().forEach(teamPosition -> {
            if (!teamPosition.getRole().equals("Leader")) {
                teamPositionMappedRole.put(teamPosition.getRole(), teamPosition);
            }
        }); // ex) teamPositionMap.put("FrontEnd", teamPosition)

        for (Member member : teamCreateRequest.getMemberList()) {
            if (teamPositionMappedRole.containsKey(member.getRole()) == true) { // 이미 있는 포지션인 경우
                teamPositionMappedRole.get(member.getRole()).updateTeamPosition(member);
            } else if (teamPositionMappedRole.containsKey(member.getRole()) == false) { // 새로 추가한 포지션인 경우
                TeamPosition newTeamPosition = TeamPosition.of(member.getRole(), 0L, member.getMaxCount(), team);
                teamPositionRepository.save(newTeamPosition);
                newTeamPositions.add(newTeamPosition);
            }
        }
        if (newTeamPositions.size() > 0) {
            makeTeamPositionTag(newTeamPositions, teamCreateRequest); // 팀의 기술 스택 종합 업데이트
        }
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
                    throw new InvalidMemberValueException(member.getMaxCount().toString());
                }
                if ((!teamUser.getRole().contains(member.getRole()) && !teamUser.getRole().equals("Leader"))
                        && teamUser.getCount() >= 1) { // FE(1/4) 상황에서 FE 역할을 삭제 한 경우
                    throw new InvalidMemberValueException(teamUser.getRole() + " - 팀원이 있을땐 역할을 삭제 할수 없습니다.");
                }
            }
        }
    }

    /**
     * 팀장이 팀 삭제하는 API
     *
     * @param leaderID
     * @param teamID
     */
    @Transactional
    public void deleteTeam(Long leaderID, Long teamID) {
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> {
                    throw new TeamNotFoundException("존재하지 않는 게시물");
                });
        if (!leaderID.equals(team.getLeaderID())) {
            throw new LeaderOnlyPermitException("팀 삭제 - teamID: " + teamID);
        }
        if (StringUtils.hasText(team.getThumbnailUploadUrl())) {
            fileService.deleteImage(team.getThumbnailUrl()); // 비용절감을 위해 삭제된 팀은 S3에서 섬네일 삭제
        }
        team.deleteTeam();
        // 팀 삭제 알림을 보내는 로직
        List<User> sendAlertTarget = teamUserRepository.findTeamUserJoinUser(teamID)
                .stream()
                .map(teamUser -> teamUser.getUser())
                .toList();
        alertCreateService.saveTeamDeleteAlert(sendAlertTarget, team);
        log.info("deleted team ID : " + teamID);
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
        if(teamById.getIsDeleted() == 1L) {
            throw new TeamNotFoundException("삭제된 게시물");
        }
        TeamTypeResponse teamType = TeamTypeResponse.fromTeamEntity(teamById);
        return teamType;
    }

    public TeamLikeResponse getTeamLikes(String authorizationHeader, Long teamId) {
        Team team = teamRepository.findTeamById(teamId).orElseThrow(() -> new TeamNotFoundException("유저가 팀에 좋아요를 눌렀는 지 확인하는 과정에서 존재하지 않는 팀 id를 요청했습니다."));
        if (authorizationHeader != null) {
            Long userId = tokenProvider.getUserId(authorizationHeader, "유저가 팀에 좋아요를 눌렀는 지 확인하는 과정에서 유효하지 않은 토큰을 받았습니다.");
            User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("유저가 팀에 좋아요를 눌렀는 지 확인하는 과정에서 존재하지 않는 유저 id를 요청했습니다."));
            Likes likes = likeRepository.findLikesByTeamAndUser(team, user).orElse(null);
            return TeamLikeResponse.builder()
                    .check(likes != null)
                    .totalLike(team.getLikes().size())
                    .build();
        } else {
            return TeamLikeResponse.builder()
                    .check(false)
                    .totalLike(team.getLikes().size())
                    .build();
        }

    }

    @Transactional
    public Long likeTeam(String authorizationHeader, Long teamId) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "팀의 좋아요를 반영하면서 유효하지 않은 토큰을 받았습니다.");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("팀의 좋아요를 반영하면서 존재하지 않는 유저 id를 받았습니다."));
        Team team = teamRepository.findTeamJoinTeamUserById(teamId).orElseThrow(() -> new TeamNotFoundException("팀의 좋아요를 반영하면서 존재하지 않는 팀 id를 받았습니다."));

        if (likeRepository.existsLikeByTeamAndUser(team, user)) {
            throw new DuplicateLikeException("이미 좋아요를 누른 사용자가 같은 팀에 요청을 보냈습니다.");
        }
        likeRepository.save(Likes.builder().user(user).team(team).build());
        alertCreateService.saveTeamLikeAlert(user, team, team.getLikes().size());
        return userId;
    }

    @Transactional
    public Long undoLikeTeam(String authorizationHeader, Long teamId) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "팀의 좋아요를 취소하는 과정에서 훼손된 토큰을 받았습니다.");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("팀의 좋아요를 취소하는 과정에서 존재하지 않는 유저 id를 받았습니다."));
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("팀의 좋아요를 취소하는 과정에서 존재하지 않는 피드 id를 받았습니다."));
        Likes like = likeRepository.findLikesByTeamAndUser(team, user).orElseThrow(() -> new CommentNotFoundException("팀의 좋아요를 취소하는 과정에서 존재하지 않는 좋아요 id를 받았습니다."));

        if (like.getUser().equals(user) && like.getTeam().equals(team)) {
            likeRepository.delete(like);
            return userId;
        } else {
            throw new ResourceNotPermitException(COMMENT_NOT_FOUND, "팀의 좋아요를 삭제하는 과정에서 존재하지 않는 댓글에 접근했거나 인가받지 못한 사용자로부터의 요청입니다.");
        }
    }
}

