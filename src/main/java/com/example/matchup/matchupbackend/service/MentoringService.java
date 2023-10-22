package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.mentoring.CreateOrEditMentoringRequest;
import com.example.matchup.matchupbackend.dto.request.mentoring.MentoringSearchParam;
import com.example.matchup.matchupbackend.dto.request.mentoring.ApplyVerifyMentorRequest;
import com.example.matchup.matchupbackend.dto.response.mentoring.MentoringDetailResponse;
import com.example.matchup.matchupbackend.dto.response.mentoring.MentoringSliceResponse;
import com.example.matchup.matchupbackend.dto.response.mentoring.VerifyMentorsSliceResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.ResourceNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.LikeRepository;
import com.example.matchup.matchupbackend.repository.mentoring.MentorVerifyRepository;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringRepository;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringTagRepository;
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
        User admin = getUser(authorizationHeader);
        admin.isAdmin();

        Slice<MentorVerify> mentorVerifySlice = mentorVerifyRepository.findAllByOrderByIdDesc(pageable);
        return VerifyMentorsSliceResponse.of(mentorVerifySlice, pageable);
    }

    private Mentoring getMentoring(Long mentoringId) {
        return mentoringRepository.findById(mentoringId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "찾을 수 없는 멘토링입니다."));
    }

    private User getUser(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader);
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("찾을 수 없는 유저입니다."));
        return user;
    }

    private User loadMentor(String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader);
        return userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("찾을 수 없는 멘토입니다."));
    }

    private Mentoring loadMentoringAndCheckAvailable(Long mentoringId, User mentor) {
        Mentoring mentoring = mentoringRepository.findById(mentoringId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND, "존재하지 않은 멘토링입니다."));
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
