package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.request.feed.FeedCreateRequest;
import com.example.matchup.matchupbackend.dto.request.feed.FeedSearchRequest;
import com.example.matchup.matchupbackend.dto.request.feed.FeedUpdateRequest;
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

import static com.example.matchup.matchupbackend.global.config.jwt.TokenProvider.HEADER_AUTHORIZATION;

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
    @PostMapping("/feed")
    public ResponseEntity<Long> createFeed(@RequestHeader(value = HEADER_AUTHORIZATION) String token,
                                          @RequestBody FeedCreateRequest request) {
        Feed feedCreated = feedService.saveFeed(request, token);
        if (feedCreated != null) {
            log.info("제목: {} 피드가 생성되었습니다.", feedCreated.getTitle());
            URI uri = URI.create("/feed");
            return ResponseEntity.created(uri).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/feed")
    public ResponseEntity<Long> updateFeed(@RequestHeader(value = HEADER_AUTHORIZATION) String token,
                                           @RequestBody FeedUpdateRequest request) {
        Feed feedUpdated = feedService.updateFeed(request, token);
        if (feedUpdated != null) {
            log.info("제목: {} 피드가 수정되었습니다.", feedUpdated.getTitle());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
