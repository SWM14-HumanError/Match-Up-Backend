package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.repository.feed.FeedRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class FeedService {

    private final FeedRepository feedRepository;

    public List<Feed> getFeedList() {
        return feedRepository.getFeedList();
    }
}
