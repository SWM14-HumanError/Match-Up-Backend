package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.service.MentoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class MentoringController {

    private final MentoringService mentoringService;

    @GetMapping("/list/mentoring")
    public ResponseEntity<List<Mentoring>> showMentorings() {

        List<Mentoring> mentoringList = mentoringService.getMentoringList();
        return (mentoringList != null)
                ? ResponseEntity.ok(mentoringList)
                : ResponseEntity.notFound().build();
    }
}
