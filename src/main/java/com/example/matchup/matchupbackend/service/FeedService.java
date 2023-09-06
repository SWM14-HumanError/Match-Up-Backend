package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.feed.FeedSearchResquestDto;
import com.example.matchup.matchupbackend.dto.feed.FeedSliceResponseDto;
import com.example.matchup.matchupbackend.repository.feed.FeedRepositoryCustom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class FeedService {

    private final FeedRepositoryCustom feedRepositoryCustom;

    public FeedSliceResponseDto getSliceFeed(FeedSearchResquestDto request, Pageable pageable) {

        return feedRepositoryCustom.findFeedListByFeedRequest(request, pageable);
    }
}
