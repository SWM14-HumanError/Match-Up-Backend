package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.feed.FeedSearchResquestDto;
import com.example.matchup.matchupbackend.dto.feed.FeedSliceResponseDto;
import com.example.matchup.matchupbackend.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/list/feed")
    public ResponseEntity<FeedSliceResponseDto> showFeeds(FeedSearchResquestDto feedSearchResquestDto,
                                                          @PageableDefault(page = 0, size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {

        FeedSliceResponseDto response = feedService.getSliceFeed(feedSearchResquestDto, pageable);
        return (response != null)
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().build();
    }
}
