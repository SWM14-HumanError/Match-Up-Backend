package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.feed.FeedCreateOrUpdateRequest;
import com.example.matchup.matchupbackend.dto.request.feed.FeedSearchRequest;
import com.example.matchup.matchupbackend.dto.response.feed.FeedSliceResponse;
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

    public FeedSliceResponse getSliceFeed(FeedSearchRequest request, Pageable pageable) {
        return feedRepositoryCustom.findFeedListByFeedRequest(request, pageable);
    }

    @Transactional
    public Feed saveFeed(FeedCreateOrUpdateRequest request, String authorizationHeader) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "createFeed 중에 훼손된 토큰을 받았습니다.");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("createFeed 중에 존재하지 않는 유저에 접근했습니다."));
        Feed feed = request.toEntity(user);
        return feedRepository.save(feed);
    }

    @Transactional
    public Feed updateFeed(FeedCreateOrUpdateRequest request, String authorizationHeader, Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new UserNotFoundException("updateFeed 중에 존재하지 않는 피드에 접근했습니다."));
        Long userId = tokenProvider.getUserId(authorizationHeader, "updateFeed 중에 훼손된 토큰을 받았습니다.");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("updateFeed 중에 존재하지 않는 유저에 접근했습니다."));

        if (feed.getUser().equals(user)) {
            Feed updatedFeed = feed.updateFeed(request);
            return updatedFeed;
        }
        else {
            throw new AuthorizeException("updateFeed 중에 인가받지 못한 유저의 접근입니다.");
        }
    }

    @Transactional
    public boolean deleteFeed(String token, Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new UserNotFoundException("deleteFeed 중에 존재하지 않는 피드에 접근했습니다."));
        Long userId = tokenProvider.getUserId(token, "deleteFeed 중에 훼손된 토큰을 받았습니다.");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("updateFeed 중에 존재하지 않는 유저에 접근했습니다."));

        if (feed.getUser().equals(user)) {
            feedRepository.delete(feed);
            return true;
        }
        else {
            return false;
        }
    }
}
