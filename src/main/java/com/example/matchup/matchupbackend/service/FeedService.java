package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.FeedSearchType;
import com.example.matchup.matchupbackend.dto.request.feed.FeedCreateOrUpdateRequest;
import com.example.matchup.matchupbackend.dto.request.feed.FeedSearchRequest;
import com.example.matchup.matchupbackend.dto.response.feed.FeedSearchResponse;
import com.example.matchup.matchupbackend.dto.response.feed.FeedSliceResponse;
import com.example.matchup.matchupbackend.entity.Comment;
import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.FeedNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.LikeRepository;
import com.example.matchup.matchupbackend.repository.comment.CommentRepository;
import com.example.matchup.matchupbackend.repository.feed.FeedRepository;
import com.example.matchup.matchupbackend.repository.feed.FeedRepositoryCustom;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class FeedService {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final FeedRepositoryCustom feedRepositoryCustom;
    private final FeedRepository  feedRepository;
    private final TokenProvider tokenProvider;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    /**
     * 유저가 로그인을 한 경우라면 유저가 표시한 좋아요 여부를 같이 응답
     * 아니라면 도메인, 작성자 혹은 피드 제목을 기준으로 필터된 값을 슬라이스하여 응답
     */
    public FeedSliceResponse getSliceFeed(FeedSearchRequest request, Pageable pageable, String authorizationHeader) {
        List<Feed> feeds = feedRepositoryCustom.findFeedListByFeedRequest(request, pageable);

        Long userId = tokenProvider.getUserId(authorizationHeader, "getSliceFeed");
        User user = (userId != null) ? userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("getSliceFeed")) : null;

        List<FeedSearchResponse> responseFeeds = new ArrayList<>();
        for (Feed feed : feeds) {
            FeedSearchResponse feedSearchResponse = FeedSearchResponse.builder().user(feed.getUser()).feed(feed).build();
            boolean isLiked = false;
            if (user != null) {
                isLiked = likeRepository.existsLikeByFeedAndUser(feed, user);
            }
            feedSearchResponse.setLiked(isLiked);
            responseFeeds.add(feedSearchResponse);
        }

        FeedSliceResponse response = new FeedSliceResponse();
        response.setFeedSearchResponses(responseFeeds);
        response.setSize(feeds.size());
        response.setHasNextSlice(hasNextSlice(request, pageable));

        return response;
    }

    // todo: refactoring, hasNextSlice할 필요없이 하나의 query로 카운트 할 수 있음.
    private boolean hasNextSlice(FeedSearchRequest request, Pageable pageable) {

        if (request.getSearchType() != null) {
            if (request.getDomain() == ProjectDomain.전체) {
                String jpql = (request.getSearchType() == FeedSearchType.TITLE)
                        ? "select count(cnt) from (select f.id cnt from Feed f where f.title like CONCAT('%', :searchValue, '%') GROUP BY f.id order by f.id DESC)"
                        : "select count(cnt) from (select f.id cnt from Feed f join f.user u on u.name like CONCAT('%', :searchValue, '%') GROUP BY f.id order by f.id DESC)";

                try {
                    Long countQuery = em.createQuery(jpql, Long.class)
                            .setParameter("searchValue", request.getSearchValue())
                            .getSingleResult();
                    return countQuery > ((pageable.getPageNumber() + 1L) * pageable.getPageSize());
                } catch (Exception e) {
                    return false;
                }
            } else {
                String jpql = (request.getSearchType() == FeedSearchType.TITLE)
                        ? "select count(cnt) from (select f.id cnt from Feed f where f.title like CONCAT('%', :searchValue, '%') and f.projectDomain = :domain GROUP BY f.id order by f.id DESC)"
                        : "select count(cnt) from (select f.id cnt from Feed f join f.user u on u.name like CONCAT('%', :searchValue, '%') and f.projectDomain = :domain GROUP BY f.id order by f.id DESC)";

                try {
                    Long countQuery = em.createQuery(jpql, Long.class)
                            .setParameter("domain", request.getDomain())
                            .setParameter("searchValue", request.getSearchValue())
                            .getSingleResult();
                    return countQuery > ((pageable.getPageNumber() + 1L) * pageable.getPageSize());
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            if (request.getDomain() == ProjectDomain.전체) {
                String jpql = "select count(cnt) from (select f.id cnt from Feed f GROUP BY f.id)";

                try {
                    Long countQuery = em.createQuery(jpql, Long.class)
                            .getSingleResult();
                    //                log.info("카운트 쿼리 " + Long.toString(countQuery));
                    return countQuery > ((pageable.getPageNumber() + 1L) * pageable.getPageSize());
                } catch (Exception e) {
                    return false;
                }
            } else {
                String jpql = "select count(cnt) from (select f.id cnt from Feed f WHERE f.projectDomain = :domain GROUP BY f.id)";

                try {
                    Long countQuery = em.createQuery(jpql, Long.class)
                            .setParameter("domain", request.getDomain())
                            .getSingleResult();
                    return countQuery > ((pageable.getPageNumber() + 1L) * pageable.getPageSize());
                } catch (Exception e) {
                    return false;
                }
            }
        }
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

    @Transactional
    public Comment createFeedComment(String authorizationHeader, Long feedId, String content) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "createFeedComment");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("createFeedComment"));
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new FeedNotFoundException("createFeedComment"));

        Comment comment = Comment.builder()
                .content(content)
                .feed(feed)
                .user(user)
                .build();

        return commentRepository.save(comment);
    }
}
