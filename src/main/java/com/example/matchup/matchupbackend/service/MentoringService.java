package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.mentoring.SliceMentoringCardResponse;
import com.example.matchup.matchupbackend.dto.request.mentoring.CreateOrEditMentoringRequest;
import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.MentoringTag;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.ResourceNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringRepository;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringTagRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;

import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_FOUND;
import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_PERMITTED;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MentoringService {

    private final UserRepository userRepository;
    private final MentoringRepository mentoringRepository;
    private final MentoringTagRepository mentoringTagRepository;
    private final TokenProvider tokenProvider;

    public SliceMentoringCardResponse showMentorings(Pageable pageable) {
        return null;
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
}
