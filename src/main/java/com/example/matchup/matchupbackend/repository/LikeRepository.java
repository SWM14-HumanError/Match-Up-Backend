package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.Likes;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    boolean existsLikeByFeedAndUser(Feed feed, User user);

    boolean existsLikeByTeamAndUser(Team team, User user);

    Optional<Likes> findLikesByFeedAndUser(Feed feed, User user);

    Optional<Likes> findLikesByTeamAndUser(Team team, User user);

    Optional<Likes> findByUserIdAndLikeReceiverId(Long userId, Long likeReceiverId);

    @Query("select l from Likes l join fetch l.team where l.user.id=:userId and l.team.type=0L")
    List<Likes> findLikesJoinProjectByUserId(@Param(value = "userId") Long userId);

    @Query("select l from Likes l join fetch l.team where l.user.id=:userId and l.team.type=1L")
    List<Likes> findLikesJoinStudyByUserId(@Param(value = "userId") Long userId);

    @Query("select l from Likes l join fetch l.likeReceiver where l.user.id=:userId")
    List<Likes> findLikesJoinUserByUserId(@Param(value = "userId") Long userId);

    boolean existsByUserIdAndAndLikeReceiverId(Long userId, Long likeReceiverId);
}
