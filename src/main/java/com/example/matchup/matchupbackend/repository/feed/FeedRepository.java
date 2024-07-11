package com.example.matchup.matchupbackend.repository.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long>{
    @Query("select f from Feed f join fetch f.user where f.id = :id")
    Optional<Feed> findFeedJoinUserById(@Param("id") Long id);
}
