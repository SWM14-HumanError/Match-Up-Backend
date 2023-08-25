package com.example.matchup.matchupbackend.repository.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Query("SELECT feed from Feed feed")
    List<Feed> getFeedList();
}
