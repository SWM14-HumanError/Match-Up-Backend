package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.feed.FeedCreateOrUpdateRequest;
import com.example.matchup.matchupbackend.dto.request.feed.FeedSearchRequest;
import com.example.matchup.matchupbackend.dto.response.feed.FeedSliceResponseDto;
import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/feeds")
    public ResponseEntity<FeedSliceResponseDto> showFeeds(FeedSearchRequest feedSearchRequest,
                                                          @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        FeedSliceResponseDto response = feedService.getSliceFeed(feedSearchRequest, pageable);
        return (response != null)
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().build();
    }

    @PostMapping("/feed")
    public ResponseEntity<Long> createFeed(@RequestHeader(value = HEADER_AUTHORIZATION) String token,
                                           @RequestBody FeedCreateOrUpdateRequest request) {
        Feed feedCreated = feedService.saveFeed(request, token);
        if (feedCreated != null) {
            log.info("id: {}의 피드가 생성되었습니다.", feedCreated.getId());
            URI uri = URI.create("/feed");
            return ResponseEntity.created(uri).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/feed/{feed_id}")
    public ResponseEntity<Long> updateFeed(@RequestHeader(value = HEADER_AUTHORIZATION) String token,
                                           @RequestBody FeedCreateOrUpdateRequest request,
                                           @PathVariable("feed_id") Long feedId) {
        Feed feedUpdated = feedService.updateFeed(request, token, feedId);
        if (feedUpdated != null) {
            log.info("id: {}의 피드가 수정되었습니다.", feedUpdated.getId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/feed/{feed_id}")
    public ResponseEntity<Long> deleteFeed(@RequestHeader(value = HEADER_AUTHORIZATION) String token,
                                           @PathVariable("feed_id") Long feedId) {
        if (feedService.deleteFeed(token, feedId)) {
            log.info("id: {}의 피드가 삭제되었습니다.", feedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
