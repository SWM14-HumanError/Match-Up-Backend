package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.feed.FeedCreateRequest;
import com.example.matchup.matchupbackend.dto.request.feed.FeedSearchRequest;
import com.example.matchup.matchupbackend.dto.request.feed.FeedUpdateRequest;
import com.example.matchup.matchupbackend.dto.response.feed.FeedSliceResponseDto;
import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.feed.FeedRepository;
import com.example.matchup.matchupbackend.repository.feed.FeedRepositoryCustom;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class FeedService {

    private final UserRepository userRepository;
    private final FeedRepositoryCustom feedRepositoryCustom;
    private final FeedRepository  feedRepository;
    private final TokenProvider tokenProvider;

    public FeedSliceResponseDto getSliceFeed(FeedSearchRequest request, Pageable pageable) {
        return feedRepositoryCustom.findFeedListByFeedRequest(request, pageable);
    }

    @Transactional
    public Feed saveFeed(FeedCreateRequest request, String token) {
        Long userId = tokenProvider.getUserId(token, "createFeed");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("createFeed"));
        Feed feed = request.toEntity(user);
        return feedRepository.save(feed);
    }

    @Transactional
    public Feed updateFeed(FeedUpdateRequest request, String token) {
        Feed feed = feedRepository.findById(request.getId()).orElseThrow(() -> new UserNotFoundException("updateFeed"));
        Long userId = tokenProvider.getUserId(token, "updateFeed");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("updateFeed"));

        if (feed.getUser().equals(user)) {
            Feed updatedFeed = feed.updateFeed(request);
            return updatedFeed;
        }
        throw new AuthorizeException("updateFeed");
    }
}
