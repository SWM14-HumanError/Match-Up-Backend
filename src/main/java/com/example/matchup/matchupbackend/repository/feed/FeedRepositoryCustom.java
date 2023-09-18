package com.example.matchup.matchupbackend.repository.feed;

import com.example.matchup.matchupbackend.dto.FeedSearchType;
import com.example.matchup.matchupbackend.dto.request.feed.FeedSearchRequest;
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

    /**
     * 피드 조회
     * 도메인(건강, 사회, 과학 등)이나 글쓴이, 글 제목으로 검색한 결과로 응답
     * 추가로 요청한 사용자가 로그인이 되어있을 경우, 피드를 좋아요 표시 여부도 응답
     */
    public List<Feed> findFeedListByFeedRequest(FeedSearchRequest request, Pageable pageable) {

        /**
         * 우선 글쓴이, 글 제목으로 검색하였는 지를 나누고
         * 그 다음에 도메인에 대해서 query 추가
         */
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
        return feeds;
    }
}
