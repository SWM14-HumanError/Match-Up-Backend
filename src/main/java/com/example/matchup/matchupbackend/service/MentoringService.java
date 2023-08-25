package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.repository.mentoring.MentoringRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class MentoringService {

    private final MentoringRepository mentoringRepository;

    public List<Mentoring> getMentoringList() {
        return mentoringRepository.getMentoringList();
    }
}
