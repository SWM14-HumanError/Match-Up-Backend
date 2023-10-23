package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.dto.request.mentoring.MentoringSearchParam;
import com.example.matchup.matchupbackend.entity.Mentoring;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MentoringRepositoryCustom {

    Slice<Mentoring> findMentoringByMentoringSearchParam(MentoringSearchParam param, Pageable pageable);
}
