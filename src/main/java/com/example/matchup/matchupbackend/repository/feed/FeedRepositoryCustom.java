package com.example.matchup.matchupbackend.repository.feed;

import com.example.matchup.matchupbackend.dto.request.feed.FeedSearchRequest;
import com.example.matchup.matchupbackend.dto.response.feed.FeedSearchResponse;
import com.example.matchup.matchupbackend.dto.FeedSearchType;
import com.example.matchup.matchupbackend.dto.response.feed.FeedSliceResponse;
import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedRepositoryCustom {

    private final EntityManager em;

    public FeedSliceResponse findFeedListByFeedRequest(FeedSearchRequest request, Pageable pageable) {

        List<Feed> feeds;
        if (request.getSearchType() != null) {
            if (request.getDomain() == ProjectDomain.전체) {
                String jpql = (request.getSearchType() == FeedSearchType.TITLE)
                        ? "SELECT f FROM Feed f WHERE f.title LIKE CONCAT('%', :searchValue, '%') ORDER BY f.id DESC"
                        : "SELECT f FROM Feed f join f.user u on u.name LIKE CONCAT('%', :searchValue, '%') ORDER BY f.id DESC";

                feeds = em.createQuery(jpql, Feed.class)
                        .setParameter("searchValue", request.getSearchValue())
                        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                        .setMaxResults(pageable.getPageSize())
                        .getResultList();
            } else {
                String jpql = (request.getSearchType() == FeedSearchType.TITLE)
                        ? "SELECT f FROM Feed f WHERE f.title LIKE CONCAT('%', :searchValue, '%') and f.projectDomain = :domain ORDER BY f.id DESC"
                        : "SELECT f FROM Feed f join f.user u on u.name LIKE CONCAT('%', :searchValue, '%') and f.projectDomain = :domain ORDER BY f.id DESC";

                feeds = em.createQuery(jpql, Feed.class)
                        .setParameter("searchValue", request.getSearchValue())
                        .setParameter("domain", request.getDomain())
                        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                        .setMaxResults(pageable.getPageSize())
                        .getResultList();
            }
        } else {
            if (request.getDomain() == ProjectDomain.전체) {
                String jpql = "SELECT f FROM Feed f ORDER BY f.id DESC";
                feeds = em.createQuery(jpql, Feed.class)
                        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                        .setMaxResults(pageable.getPageSize())
                        .getResultList();
            } else {
                String jpql = "SELECT f FROM Feed f WHERE f.projectDomain = :domain ORDER BY f.id DESC";
                feeds = em.createQuery(jpql, Feed.class)
                        .setParameter("domain", request.getDomain())
                        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                        .setMaxResults(pageable.getPageSize())
                        .getResultList();
            }
        }

        List<FeedSearchResponse> responseFeeds = feeds.stream().map(feed -> FeedSearchResponse.builder()
                .user(feed.getUser())
                .feed(feed)
                .build()).toList();

        FeedSliceResponse response = new FeedSliceResponse();
        response.setFeedSearchResponses(responseFeeds);
        response.setSize(feeds.size());
        response.setHasNextSlice(hasNextSlice(request, pageable));

        return response;
    }

    // 검토. hasNextSlice할 필요없이 하나의 query로 카운트 할 수 있음.
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
}
