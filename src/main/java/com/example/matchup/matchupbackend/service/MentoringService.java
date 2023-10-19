package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.mentoring.SliceMentoringCardResponse;
import com.example.matchup.matchupbackend.dto.request.mentoring.CreateMentoringRequest;
import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MentoringService {

    private final UserRepository userRepository;
    private final MentoringRepository mentoringRepository;
    private final TokenProvider tokenProvider;

    public SliceMentoringCardResponse showMentorings(Pageable pageable) {
        return null;
    }

    @Transactional
    public void createMentoring(String authorizationHeader, CreateMentoringRequest request) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "멘토링을 생성하는 중입니다.");
        User mentor = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("멘토링을 생성하면서 존재하지 않는 유저 id로 요청했습니다"));

        Mentoring mentoring = Mentoring.create(request, mentor);
        mentoringRepository.save(mentoring);
    }
}
