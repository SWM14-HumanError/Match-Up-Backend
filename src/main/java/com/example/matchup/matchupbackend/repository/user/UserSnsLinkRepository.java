package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.entity.UserProfile;
import com.example.matchup.matchupbackend.entity.UserSnsLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSnsLinkRepository extends JpaRepository<UserSnsLink, Long> {
    void deleteByUserProfile(UserProfile userProfile);
}
