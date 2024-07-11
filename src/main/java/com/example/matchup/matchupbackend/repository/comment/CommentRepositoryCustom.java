package com.example.matchup.matchupbackend.repository.comment;

import com.example.matchup.matchupbackend.entity.Comment;
import com.example.matchup.matchupbackend.entity.Feed;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustom {

    private final EntityManager em;

    public List<Comment> findFeedCommentsByFeed(Feed feed, Pageable pageable) {
        return em.createQuery("select c from Comment c where c.feed = :feed order by c.id DESC", Comment.class)
                .setParameter("feed", feed)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}
