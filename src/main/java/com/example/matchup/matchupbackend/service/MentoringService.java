package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyMentoringRequest;
import com.example.matchup.matchupbackend.dto.request.mentoring.CreateOrEditMentoringRequest;
import com.example.matchup.matchupbackend.dto.request.mentoring.MentoringSearchParam;
import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyVerifyMentorRequest;
import com.example.matchup.matchupbackend.dto.response.mentoring.*;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.ResourceNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.LikeRepository;
import com.example.matchup.matchupbackend.repository.mentoring.MentorVerifyRepository;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringRepository;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringTagRepository;
import com.example.matchup.matchupbackend.repository.mentoring.TeamMentoringRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.matchup.matchupbackend.entity.ApplyStatus.WAITING;
import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_FOUND;
import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_PERMITTED;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MentoringService {

    private final LikeRepository likeRepository;
    private final MentoringRepository mentoringRepository;
    private final MentoringTagRepository mentoringTagRepository;
    private final MentorVerifyRepository mentorVerifyRepository;
    private final TeamRepository teamRepository;
    private final TeamMentoringRepository teamMentoringRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    /*
    로그인한 유저가 멘토링 목록을 조회하면 자신이 누른 좋아요를 확인할 수 있어야하므로 토큰을 받는다.
     */
    public MentoringSliceResponse showMentorings(String authorizationHeader, @Valid MentoringSearchParam param, Pageable pageable) {
        User user = (authorizationHeader != null) ? loadMentor(authorizationHeader) : null;

        // 검색한 결과를 페이징
        Slice<Mentoring> pageOfMentoringSearchSlice = mentoringRepository.findMentoringByMentoringSearchParam(param, pageable);
        List<Mentoring> mentorings = pageOfMentoringSearchSlice.getContent();
        // 멘토링에서 좋아요 수로 매핑
        Map<Mentoring, Long> mentoringToLikesCountMap = mentorings.stream()
                .collect(Collectors.toMap(m -> m, likeRepository::countByMentoring));
        // 멘토링에서 좋아요 눌렀는 지를 매핑
        if (user != null) {
            Map<Mentoring, Boolean> mentoringToCheckLikeMap = mentorings.stream()
                    .collect(Collectors.toMap(m -> m, mentoring -> likeRepository.existsByUserAndMentoring(user, mentoring)));
            return MentoringSliceResponse.of(pageOfMentoringSearchSlice, mentoringToLikesCountMap, mentoringToCheckLikeMap);
        }

        return MentoringSliceResponse.of(pageOfMentoringSearchSlice, mentoringToLikesCountMap, Collections.emptyMap());
    }

    @Transactional
    public void createMentoring(String authorizationHeader, CreateOrEditMentoringRequest request) {
        User mentor = loadMentor(authorizationHeader);
        isAvailableCreateMentoring(mentor);

        Mentoring mentoring = Mentoring.create(request, mentor);
        mentoringRepository.save(mentoring);
    }

    @Transactional
    public void editMentoring(String authorizationHeader, CreateOrEditMentoringRequest request, Long mentoringId) {
        User mentor = loadMentor(authorizationHeader);
        Mentoring mentoring = loadMentoringAndCheckAvailable(mentoringId, mentor);

        mentoringTagRepository.deleteAll(mentoring.getMentoringTags());
        List<MentoringTag> mentoringTags = request.getStacks().stream()
                .map(stack -> MentoringTag.builder()
                        .tagName(stack)
                        .mentoring(mentoring)
                        .build())
                .toList();

        mentoring.edit(request, mentoringTags);
    }

    @Transactional
    public void deleteMentoring(String authorizationHeader, Long mentoringId) {
        User mentor = loadMentor(authorizationHeader);
        Mentoring mentoring = loadMentoringAndCheckAvailable(mentoringId, mentor);

        mentoring.delete();
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

    public MentoringDetailResponse showMentoringDetail(Long mentoringId) {
        Mentoring mentoring = getMentoring(mentoringId);

        return MentoringDetailResponse.of(mentoring);
    }

    @Transactional
    public void verifyMentor(ApplyVerifyMentorRequest request, String authorizationHeader) {
        User user = getUser(authorizationHeader);
        isAvailableMentor(user);

        MentorVerify mentorVerify = MentorVerify.create(request, user);

        mentorVerifyRepository.save(mentorVerify);
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
    }

    @Transactional
    public void refuseVerifyMentors(String authorizationHeader, Long verifyId) {
        isAdmin(authorizationHeader);

        MentorVerify mentorVerify = mentorVerifyRepository.findById(verifyId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "요청한 멘토 인증이 존재하지 않습니다."));

        mentorVerifyRepository.delete(mentorVerify);
    }

    public VerifyMentorsResponse showVerifyMentorEditForm(String authorizationHeader) {
        User user = getUser(authorizationHeader);
        MentorVerify mentorVerify = mentorVerifyRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "멘토 인증 신청을 하지 않은 사용자입니다."));

        return VerifyMentorsResponse.of(mentorVerify);
    }

    @Transactional
    public void editVerifyMentor(@Valid ApplyVerifyMentorRequest request, String authorizationHeader) {
        User user = getUser(authorizationHeader);
        MentorVerify mentorVerify = mentorVerifyRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "멘토 인증 신청을 하지 않은 사용자입니다."));

        mentorVerify.edit(request);
    }

    public List<TeamInfoResponse> getApplyMentoringForm(String authorizationHeader) {
        User leader = getUser(authorizationHeader);
        List<Team> teams = teamRepository.findByLeaderIDAndIsDeletedAndType(leader.getId(), 0L, 0L);

        return teams.stream().map(TeamInfoResponse::of).toList();
    }

    @Transactional
    public void applyMentoring(ApplyMentoringRequest request, Long mentoringId, String authorizationHeader) {
        User leader = getUser(authorizationHeader);
        Team team = teamRepository.findTeamByIdAndIsDeleted(request.getTeamId(), 0L).orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀입니다."));
        Mentoring mentoring = mentoringRepository.findByIdAndIsDeleted(mentoringId, false).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "존재하지 않는 멘토링입니다."));

        isAvailableApplyMentoring(leader, team, mentoring);

        TeamMentoring teamMentoring = TeamMentoring.create(mentoring, team, request);
        teamMentoringRepository.save(teamMentoring);
    }

    @Transactional
    public void acceptApplyMentoring(Long applyId, String comment, String authorizationHeader) {
        User mentor = getUser(authorizationHeader);
        TeamMentoring teamMentoring = teamMentoringRepository.findById(applyId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "찾을 수 없는 멘토링 신청입니다."));

        isAvailableAcceptOrRefuseMentoring(mentor, teamMentoring);

        teamMentoring.acceptMentoring();
    }

    @Transactional
    public void refuseApplyMentoring(Long applyId, String comment, String authorizationHeader) {
        User mentor = getUser(authorizationHeader);
        TeamMentoring teamMentoring = teamMentoringRepository.findById(applyId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "찾을 수 없는 멘토링 신청입니다."));

        isAvailableAcceptOrRefuseMentoring(mentor, teamMentoring);

        teamMentoring.refuseMentoring();
    }

    public List<MentoringApplyListResponse> showApplyMentoringList(String authorizationHeader) {
        User mentor = getUser(authorizationHeader);
        List<Mentoring> mentorings = mentoringRepository.findALlByMentorOrderByIdDesc(mentor);

        List<TeamMentoring> teamMentorings = mentorings.stream()
                .flatMap(mentoring -> mentoring.getTeamMentoringList().stream()
                        .filter(teamMentoring -> teamMentoring.getStatus() == WAITING))
                .toList();

        return teamMentorings.stream().map(MentoringApplyListResponse::of).toList();
    }

    private void isAvailableAcceptOrRefuseMentoring(User mentor, TeamMentoring teamMentoring) {
        if (teamMentoring.getStatus() != WAITING) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "멘토링이 이미 처리되었습니다.");
        }
        if (!teamMentoring.getMentoring().getMentor().equals(mentor)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "요청한 멘터링 신청의 멘토가 아닙니다.");
        }
    }

    private void isAvailableApplyMentoring(User leader, Team team, Mentoring mentoring) {
        if (!team.getLeaderID().equals(leader.getId())) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "리더가 아닌 유저가 멘토링을 신청했습니다.");
        }
        if (alreadyApply(team, mentoring)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "이미 멘토링에 지원했습니다.");
        }
        if (mentoring.getMentor().equals(leader)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "자신의 멘토링에 신청할 수 없습니다.");
        }
    }

    private boolean alreadyApply(Team team, Mentoring mentoring) {
        return team.getTeamMentoringList().stream().anyMatch(teamMentoring -> teamMentoring.getMentoring().equals(mentoring) && teamMentoring.getStatus() == WAITING);
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

    private User loadMentor(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader);
        return userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("찾을 수 없는 멘토입니다."));
    }

    private Mentoring loadMentoringAndCheckAvailable(Long mentoringId, User mentor) {
        Mentoring mentoring = mentoringRepository.findByIdAndIsDeleted(mentoringId, false).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "존재하지 않은 멘토링입니다."));
        if (!mentoring.getMentor().equals(mentor)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "권한이 없는 유저의 접근입니다.");
        }

        return mentoring;
    }

    private void isAvailableCreateMentoring(User mentor) {
        if (!mentor.getIsMentor()) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "멘토링을 생성할 수 없는 유저입니다.");
        }
    }

    private void isAvailableMentor(User user) {
        if (user.getIsMentor()) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "이미 인증된 멘토입니다.");
        }
        if (mentorVerifyRepository.existsByUser(user)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "관리자의 승인을 기다리는 멘토입니다.");
        }
    }
}
