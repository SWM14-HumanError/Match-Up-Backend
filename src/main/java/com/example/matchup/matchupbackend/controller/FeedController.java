package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.service.FeedService;
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
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/list/feed")
    public ResponseEntity<List<Feed>> showFeeds() {

        List<Feed> feedList = feedService.getFeedList();
        return (feedList != null)
                ? ResponseEntity.ok(feedList)
                : ResponseEntity.notFound().build();
    }
}
