package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.Likes;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    boolean existsLikeByFeedAndUser(Feed feed, User user);
    boolean existsLikeByTeamAndUser(Team team, User user);

    Optional<Likes> findLikesByFeedAndUser(Feed feed, User user);
    Optional<Likes> findLikesByTeamAndUser(Team team, User user);
    Optional<Likes> findByUserIdAndLikeReceiverId(Long userId,Long likeReceiverId);
}
