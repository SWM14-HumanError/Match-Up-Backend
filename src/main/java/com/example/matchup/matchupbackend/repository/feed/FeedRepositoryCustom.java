package com.example.matchup.matchupbackend.repository.feed;

import com.example.matchup.matchupbackend.dto.feed.FeedSearchResponseDto;
import com.example.matchup.matchupbackend.dto.feed.FeedSearchResquestDto;
import com.example.matchup.matchupbackend.dto.feed.FeedSliceResponseDto;
import com.example.matchup.matchupbackend.dto.feed.SearchType;
import com.example.matchup.matchupbackend.entity.Feed;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FeedRepositoryCustom {

    private final EntityManager em;

    public FeedSliceResponseDto findFeedListByFeedRequest(FeedSearchResquestDto request, Pageable pageable) {
        log.info("request : {}", request.toString());

        String jpql = (request.getSearchType().equals(SearchType.TITLE))
                ? "SELECT f FROM Feed f WHERE f.title LIKE CONCAT('%', :searchValue, '%') ORDER BY f.id DESC"
                : "SELECT f FROM Feed f join f.user u on u.name LIKE CONCAT('%', :searchValue, '%') ORDER BY f.id DESC";

        List<Feed> feeds = em.createQuery(jpql, Feed.class)
                .setParameter("searchValue", request.getSearchValue())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        List<FeedSearchResponseDto> responseFeeds = feeds.stream().map(feed -> FeedSearchResponseDto.builder()
                .user(feed.getUser())
                .feed(feed)
                .build()).toList();

        FeedSliceResponseDto response = new FeedSliceResponseDto();
        response.setFeedSearchResponsDtos(responseFeeds);
        response.setSize(feeds.size());
        response.setHasNextSlice(hasNextSlice(request, pageable));

        return response;
    }

    private boolean hasNextSlice(FeedSearchResquestDto request, Pageable pageable) {

        String jpql = (request.getSearchType().equals(SearchType.TITLE))
                ? "select count(f.id) from Feed f where f.title like CONCAT('%', :searchValue, '%') GROUP BY f.id order by f.id DESC"
                : "select count(f.id) from Feed f join f.user u on u.name like CONCAT('%', :searchValue, '%') GROUP BY f.id order by f.id DESC";

        try {
            Long countQuery = em.createQuery(jpql, Long.class)
                    .setParameter("searchValue", request.getSearchValue())
                    .getSingleResult();
            return countQuery > (pageable.getPageNumber() * pageable.getPageSize());
        } catch (Exception e) {
            return false;
        }
    }
}
