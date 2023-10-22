package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.entity.MentorVerify;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorVerifyRepository extends JpaRepository<MentorVerify, Long> {

    Boolean existsByUser(User user);

    Slice<MentorVerify> findAllByOrderByIdDesc(Pageable pageable);
}
