package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.mentoring.*;
import com.example.matchup.matchupbackend.dto.response.mentoring.*;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.ResourceNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.LikeRepository;
import com.example.matchup.matchupbackend.repository.mentoring.*;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.teamuser.TeamUserRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.matchup.matchupbackend.entity.ApplyStatus.*;
import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_FOUND;
import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_PERMITTED;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MentoringService {

    private final AlertCreateService alertCreateService;
    private final LikeRepository likeRepository;
    private final FileService fileService;
    private final MentoringRepository mentoringRepository;
    private final MentoringTagRepository mentoringTagRepository;
    private final MentorVerifyRepository mentorVerifyRepository;
    private final TeamRepository teamRepository;
    private final TeamMentoringRepository teamMentoringRepository;
    private final TeamUserRepository teamUserRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ReviewMentorRepository reviewMentorRepository;

    public MentoringSliceResponse showMentoringsInMentoringPage(String authorizationHeader, MentoringSearchParam param, Pageable pageable) {
        // 로그인한 유저는 좋아요 표시를 위해 token을 받을 수 있다.
        User user = (authorizationHeader != null) ? getUser(authorizationHeader) : null;

        Slice<Mentoring> pageOfMentoringSearchSlice = mentoringRepository.findMentoringByMentoringSearchParam(param, pageable);
        List<Mentoring> mentorings = pageOfMentoringSearchSlice.getContent();
        Map<Mentoring, Long> mentoringToLikesCountMap = mentorings.stream()
                .collect(Collectors.toMap(m -> m, likeRepository::countByMentoring));

        if (user != null) {
            Map<Mentoring, Boolean> mentoringToCheckLikeMap = mentorings.stream()
                    .collect(Collectors.toMap(m -> m, mentoring -> likeRepository.existsByUserAndMentoring(user, mentoring)));
            return MentoringSliceResponse.of(pageOfMentoringSearchSlice, mentoringToLikesCountMap, mentoringToCheckLikeMap);
        }

        return MentoringSliceResponse.of(pageOfMentoringSearchSlice, mentoringToLikesCountMap, Collections.emptyMap());
    }

    @Transactional
    public Long createMentoringByMentor(String authorizationHeader, CreateOrEditMentoringRequest request) {
        User mentor = getMentor(authorizationHeader);
        isAvailableCreateMentoring(mentor);

        Mentoring mentoring = Mentoring.create(request, mentor);
        // 사진 저장
        if (request.getImageBase64() != null) {
            UploadFile uploadFile = fileService.storeBase64ToFile(request.getImageBase64(), request.getImageName());
            mentoring.setUploadFile(uploadFile);
        }

        mentoringRepository.save(mentoring);
        return mentoring.getId();
    }

    @Transactional
    public Long editMentoringByMentor(String authorizationHeader, CreateOrEditMentoringRequest request, Long mentoringId) {
        User mentor = getMentor(authorizationHeader);
        Mentoring mentoring = loadMentoringAndCheckAvailable(mentoringId, mentor);

        // 사진 저장
        if (request.getImageBase64() != null) {
            UploadFile uploadFile = fileService.storeBase64ToFile(request.getImageBase64(), request.getImageName());
            mentoring.setUploadFile(uploadFile);
        }

        mentoringTagRepository.deleteAll(mentoring.getMentoringTags());
        List<MentoringTag> mentoringTags = request.getStacks().stream()
                .map(stack -> MentoringTag.builder()
                        .tagName(stack)
                        .mentoring(mentoring)
                        .build())
                .toList();

        mentoring.edit(request, mentoringTags);
        return mentoring.getId();
    }

    @Transactional
    public Long deleteMentoringByMentor(String authorizationHeader, Long mentoringId) {
        User mentor = getMentor(authorizationHeader);
        Mentoring mentoring = loadMentoringAndCheckAvailable(mentoringId, mentor);

        mentoring.delete();
        return mentoring.getId();
    }

    @Transactional
    public void pushLikeOfMentoring(String authorizationHeader, Long mentoringId) {
        User user = getUser(authorizationHeader);
        Mentoring mentoring = getMentoring(mentoringId);

        Likes likeMentoring = Likes.builder()
                .mentoring(mentoring)
                .user(user)
                .build();
        likeRepository.save(likeMentoring);
    }

    @Transactional
    public void undoLikeOfMentoring(String authorizationHeader, Long mentoringId) {
        User user = getUser(authorizationHeader);
        Mentoring mentoring = getMentoring(mentoringId);
        Likes like = likeRepository.findByUserAndMentoring(user, mentoring);

        likeRepository.delete(like);
    }

    public MentoringSearchResponse showMentoringDetail(Long mentoringId) {
        Mentoring mentoring = getMentoring(mentoringId);
        Long likes = likeRepository.countByMentoring(mentoring);

        return MentoringSearchResponse.ofDetail(mentoring, likes);
    }

    @Transactional
    public void verifyMentor(ApplyVerifyMentorRequest request, String authorizationHeader) {
        User user = getUser(authorizationHeader);
        isAvailableMentor(user);

        // MentorVerify 생성
        MentorVerify mentorVerify = MentorVerify.create(request, user);

        // 사진 저장
        if (request.getImageBase64() != null) {
            UploadFile uploadFile = fileService.storeBase64ToFile(request.getImageBase64(), request.getImageName());
            mentorVerify.setUploadFile(uploadFile);
        }

        mentorVerifyRepository.save(mentorVerify);
        alertCreateService.verifyMentorCreateAlert(user);
    }

    @Transactional
    public void editVerifyMentor(ApplyVerifyMentorRequest request, String authorizationHeader) {
        User user = getUser(authorizationHeader);
        MentorVerify mentorVerify = mentorVerifyRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "멘토 인증 신청을 하지 않은 사용자입니다."));

        // 사진 저장
        if (request.getImageBase64() != null) {
            UploadFile uploadFile = fileService.storeBase64ToFile(request.getImageBase64(), request.getImageName());
            mentorVerify.setUploadFile(uploadFile);
        }
        mentorVerify.edit(request);

        alertCreateService.editVerifyMentorCreateAlert(user);
    }

    public VerifyMentorsSliceResponse showVerifyMentors(String authorizationHeader, Pageable pageable) {
        isAdmin(authorizationHeader);

        Slice<MentorVerify> mentorVerifySlice = mentorVerifyRepository.findAllByOrderByIdDesc(pageable);
        return VerifyMentorsSliceResponse.of(mentorVerifySlice, pageable);
    }

    @Transactional
    public void acceptVerifyMentors(String authorizationHeader, Long verifyId) {
        isAdmin(authorizationHeader);

        MentorVerify mentorVerify = mentorVerifyRepository.findById(verifyId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "요청한 멘토 인증이 존재하지 않습니다."));
        User acceptedMentor = mentorVerify.getUser();
        acceptedMentor.acceptMentor();

        mentorVerifyRepository.delete(mentorVerify);

        alertCreateService.acceptVerifyMentorCreateAlert(acceptedMentor);
    }

    @Transactional
    public void refuseVerifyMentors(String authorizationHeader, Long verifyId, String comment) {
        isAdmin(authorizationHeader);

        MentorVerify mentorVerify = mentorVerifyRepository.findById(verifyId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "요청한 멘토 인증이 존재하지 않습니다."));

        mentorVerifyRepository.delete(mentorVerify);

        alertCreateService.refuseVerifyMentorCreateAlert(mentorVerify.getUser(), comment);
    }

    public VerifyMentorsResponse showVerifyMentorEditForm(String authorizationHeader) {
        User user = getUser(authorizationHeader);
        MentorVerify mentorVerify = mentorVerifyRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "멘토 인증 신청을 하지 않은 사용자입니다."));

        return VerifyMentorsResponse.of(mentorVerify);
    }

    public List<TeamInfoResponse> getApplyMentoringInputForm(String authorizationHeader) {
        User leader = getUser(authorizationHeader);
        List<Team> teams = teamRepository.findByLeaderIDAndIsDeletedAndType(leader.getId(), 0L, 0L);

        return teams.stream().map(TeamInfoResponse::of).toList();
    }

    @Transactional
    public void applyMentoringByLeader(ApplyMentoringRequest request, Long mentoringId, String authorizationHeader) {
        User leader = getUser(authorizationHeader);
        Team team = teamRepository.findTeamByIdAndIsDeleted(request.getTeamId(), 0L).orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀입니다."));
        Mentoring mentoring = mentoringRepository.findByIdAndIsDeleted(mentoringId, false).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "존재하지 않는 멘토링입니다."));

        isAvailableApplyMentoring(leader, team, mentoring);

        TeamMentoring teamMentoring = TeamMentoring.create(mentoring, team, request);
        teamMentoringRepository.save(teamMentoring);

        alertCreateService.mentoringApplyAlertToMentor(mentoring.getMentor(), team);
    }

    @Transactional
    public void acceptApplyMentoringByMentor(Long applyId, String comment, String authorizationHeader) {
        User mentor = getMentor(authorizationHeader);
        TeamMentoring teamMentoring = teamMentoringRepository.findById(applyId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "찾을 수 없는 멘토링 신청입니다."));
        User leader = userRepository.findById(teamMentoring.getTeam().getLeaderID()).orElseThrow(() -> new UserNotFoundException("찾을 수 없는 리더입니다."));

        isAvailableAcceptOrRefuseMentoring(mentor, teamMentoring);

        teamMentoring.acceptMentoring();

        alertCreateService.acceptMentoringCreateAlert(teamMentoring.getMentoring(), comment, leader);
    }

    @Transactional
    public void refuseApplyMentoringByMentor(Long applyId, String comment, String authorizationHeader) {
        User mentor = getMentor(authorizationHeader);
        TeamMentoring teamMentoring = teamMentoringRepository.findById(applyId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "찾을 수 없는 멘토링 신청입니다."));
        User leader = userRepository.findById(teamMentoring.getTeam().getLeaderID()).orElseThrow(() -> new UserNotFoundException("찾을 수 없는 리더입니다."));

        isAvailableAcceptOrRefuseMentoring(mentor, teamMentoring);

        teamMentoring.refuseMentoring();

        alertCreateService.refuseMentoringCreateAlert(teamMentoring.getMentoring(), comment, leader);
    }

    public List<MentoringApplyListResponse> showApplyMentoringList(String authorizationHeader) {
        User mentor = getMentor(authorizationHeader);
        List<Mentoring> mentorings = mentoringRepository.findALlByMentorAndIsDeletedOrderByIdDesc(mentor, false);

        List<TeamMentoring> teamMentorings = mentorings.stream()
                .flatMap(mentoring -> mentoring.getTeamMentoringList().stream()
                        .filter(teamMentoring -> teamMentoring.getStatus() == WAITING))
                .toList();

        return teamMentorings.stream().map(MentoringApplyListResponse::of).toList();
    }

    /**
     * 멘티가 멘토링에 리뷰를 남긴다.
     * 멘토링이 종료되면 종료 한 달 후까지 평가할 수 있고, 이후에는 평가할 수 없다.
     * 3개의 문항의 평균으로 score을 업데이트한다.
     */
    @Transactional
    public void reviewMentoringByMentee(ReviewMentoringRequest request, String authorizationHeader, Long mentoringId, Long teamId) {
        User mentee = getUser(authorizationHeader);
        Mentoring mentoring = getMentoring(mentoringId);
        Team team = teamRepository.findTeamByIdAndIsDeleted(teamId, 0L).orElseThrow(() -> new TeamNotFoundException("팀을 찾을 수 없습니다."));
        List<TeamMentoring> teamMentorings = getTeamMentoringsSortByEndedDateDesc(mentoring, team);

        TeamMentoring latestEndedTeamMentoring = teamMentorings.get(0);
        isAvailableReviewMentorings(mentee, team, latestEndedTeamMentoring);

        Double score = calculateAverageScore(request, mentoring);
        mentoring.setScoreAfterReviewFromMentee(score);

        ReviewMentor reviewMentor = ReviewMentor.create(request, mentoring, latestEndedTeamMentoring, mentee);
        reviewMentorRepository.save(reviewMentor);
    }

    @Transactional
    public void endMentoringByMentor(String authorizationHeader, Long teamMentoringId) {
        User mentor = getMentor(authorizationHeader);
        TeamMentoring teamMentoring = teamMentoringRepository.findById(teamMentoringId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "존재하지 않는 팀멘토링입니다."));
        isAvailableEndMentoring(mentor, teamMentoring);

        teamMentoring.endMentoring();
    }

    public List<MentoringSearchResponse> showActiveMentoringOnMentorPage(String authorizationHeader) {
        User mentor = getMentor(authorizationHeader);
        List<Mentoring> mentorings = mentoringRepository.findAllByMentorAndIsDeleted(mentor, false);
        List<TeamMentoring> teamMentorings = teamMentoringRepository.findALlByMentoringInAndStatus(mentorings, ACCEPTED);

        return teamMentorings.stream()
                    .map(teamMentoring -> MentoringSearchResponse.ofActiveMentoringInMentorPrivatePage(
                                teamMentoring.getMentoring(),
                                likeRepository.countByMentoring(teamMentoring.getMentoring()),
                                likeRepository.existsByUserAndMentoring(mentor, teamMentoring.getMentoring()),
                                teamMentoring.getStatus(),
                                teamMentoring.getTeam(),
                                teamMentoring.getId(),
                                userRepository.findNicknameById(teamMentoring.getTeam().getLeaderID())
                            )
                    ).toList();
    }

    public List<MentoringSearchResponse> showMentoringLiked(String authorizationHeader) {
        User user = getUser(authorizationHeader);
        List<Likes> mentoringLikes = likeRepository.findByUserAndMentoringNotNull(user);

        return mentoringLikes.stream()
                .map(Likes::getMentoring)
                .map(mentoring -> MentoringSearchResponse.ofSearch(mentoring, likeRepository.countByMentoring(mentoring), true))
                .toList();
    }

    public List<MentoringSearchResponse> showMentoringMine(String authorizationHeader) {
        User mentor = getMentor(authorizationHeader);
        List<Mentoring> mentorings = mentoringRepository.findAllByMentor(mentor);

        return mentorings.stream()
                .map(mentoring -> MentoringSearchResponse.ofSearch(
                        mentoring,
                        likeRepository.countByMentoring(mentoring),
                        likeRepository.existsByUserAndMentoring(mentor, mentoring))
                )
                .toList();
    }

    /**
     * 멘토링에 참여한 유저는 리뷰를 남길 수 있다.
     * 멘토링이 종료되었으며
     * 유저는 멘토링에 지원한 팀의 팀원이여야 하고
     * 멘토링이 종료된 시점에서 한 달 내에
     * 한 번만 리뷰할 수 있다.
     */
    private void isAvailableReviewMentorings(User user, Team team, TeamMentoring latestEndedTeamMentoring) {
        LocalDate endedDate = latestEndedTeamMentoring.getEndedDate();

        if (!teamUserRepository.existsByTeamAndUserAndApprove(team, user, true)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "멘토링에 지원한 팀원이 아닙니다.");
        }

        LocalDate oneMonthLater = endedDate.plus(31, ChronoUnit.DAYS);
        if (!endedDate.isBefore(oneMonthLater)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "리뷰는 멘토링 종료 후 한 달 내에만 가능합니다.");
        }

        if (reviewMentorRepository.existsByTeamMentoringAndMentee(latestEndedTeamMentoring, user)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "리뷰는 한 번만 가능합니다.");
        }
    }

    /**
     * 한 팀이 한 멘토링을 여러 번 수강할 수도 있으므로
     * 각 각에 대해서 검증한다.
     */
    private void isAvailableReviewMentoring(TeamMentoring teamMentoring) {

    }

    /**
     * 팀이 멘토링에 신청하면 멘토는 거절하거나 승락할 수 있다.
     * 해당 멘토링에 멘토이여야 하며,
     * 신청한 멘토링의 상태가 WAITING 일 때만 처리할 수 있다.
     */
    private void isAvailableAcceptOrRefuseMentoring(User mentor, TeamMentoring teamMentoring) {
        if (teamMentoring.getStatus() != WAITING) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "멘토링이 이미 처리되었습니다.");
        }
        if (!teamMentoring.getMentoring().getMentor().equals(mentor)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "요청한 멘터링 신청의 멘토가 아닙니다.");
        }
    }

    /**
     * 멘토링에 신청하려면
     * 팀의 리더만이 신청할 수 있고
     * 지원한 멘토링의 상태가 WAITING과 ACCEPTED 가 있다면 처리될 때까지 신청할 수 없다. 종료된 멘토링은 ENDED 로 전환된다.
     * 멘토는 자신의 멘토링에 신청할 수 없다.
     */
    private void isAvailableApplyMentoring(User leader, Team team, Mentoring mentoring) {
        if (!team.getLeaderID().equals(leader.getId())) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "리더가 아닌 유저가 멘토링을 신청했습니다.");
        }
        if (alreadyApplyOrProgressed(team, mentoring)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "이미 멘토링에 지원했습니다.");
        }
        if (mentoring.getMentor().equals(leader)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "자신의 멘토링에 신청할 수 없습니다.");
        }
    }

    /**
     * 오직 멘토 인증을 받은 유저만이 멘토링을 만들 수 있다.
     */
    private void isAvailableCreateMentoring(User mentor) {
        if (!mentor.getIsMentor()) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "멘토링을 생성할 수 없는 유저입니다.");
        }
    }

    /**
     * 멘토 인증을 받기 위해서는
     * 멘토 인증이 안된 유저이여야 하고
     * 멘토 인증이 처리되기 전에 다시 진행할 수 없다.
     */
    private void isAvailableMentor(User user) {
        if (user.getIsMentor()) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "이미 인증된 멘토입니다.");
        }
        if (mentorVerifyRepository.existsByUser(user)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "관리자의 승인을 기다리는 멘토입니다.");
        }
    }

    /**
     * 멘토가 진행중인 멘토링을 종료합니다.
     * 멘토링은 ACCEPTED 상태이여야하고
     * 멘토링의 멘토만이 종료할 수 있습니다.
     */
    private void isAvailableEndMentoring(User mentor, TeamMentoring teamMentoring) {
        if (teamMentoring.getStatus() != ACCEPTED) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "멘토링이 진행 중인 상태가 아닙니다.");
        }

        if (!mentor.equals(teamMentoring.getMentoring().getMentor())) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "멘토링의 멘토가 아닙니다.");
        }
    }

    private boolean alreadyApplyOrProgressed(Team team, Mentoring mentoring) {
        return team.getTeamMentoringList().stream().anyMatch(teamMentoring -> teamMentoring.getMentoring().equals(mentoring) && (teamMentoring.getStatus() == WAITING || teamMentoring.getStatus() == ACCEPTED));
    }

    private void isAdmin(String authorizationHeader) {
        User admin = getUser(authorizationHeader);
        admin.isAdmin();
    }

    private Mentoring getMentoring(Long mentoringId) {
        return mentoringRepository.findByIdAndIsDeleted(mentoringId, false).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "찾을 수 없는 멘토링입니다."));
    }

    private User getUser(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader);
        return userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("찾을 수 없는 유저입니다."));
    }

    private User getMentor(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader);
        User mentor = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("찾을 수 없는 멘토입니다."));
        if (!mentor.getIsMentor()) {
            throw new AuthorizeException("멘토가 아닙니다.");
        }

        return mentor;
    }

    private Mentoring loadMentoringAndCheckAvailable(Long mentoringId, User mentor) {
        Mentoring mentoring = mentoringRepository.findByIdAndIsDeleted(mentoringId, false).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "존재하지 않은 멘토링입니다."));
        if (!mentoring.getMentor().equals(mentor)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "권한이 없는 유저의 접근입니다.");
        }

        return mentoring;
    }

    private Double calculateAverageScore(ReviewMentoringRequest request, Mentoring mentoring) {
        double avgScore = (request.getSatisfaction() + request.getExpertise() + request.getPunctuality()) / 3.0;
        int reviewCount = reviewMentorRepository.countByMentoring(mentoring);

        return (mentoring.getScore() * reviewCount + avgScore) / (reviewCount + 1);
    }

    private List<TeamMentoring> getTeamMentoringsSortByEndedDateDesc(Mentoring mentoring, Team team) {
        List<TeamMentoring> teamMentorings = teamMentoringRepository.findAllByTeamAndStatusAndMentoringOrderByEndedDateDesc(team, ENDED, mentoring);
        if (teamMentorings.isEmpty()) {
            throw new ResourceNotFoundException(NOT_FOUND, "종료된 멘토링이 없습니다.");
        }
        return teamMentorings;
    }
}
