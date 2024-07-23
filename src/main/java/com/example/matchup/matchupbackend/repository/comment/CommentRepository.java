package com.example.matchup.matchupbackend.repository.comment;

import com.example.matchup.matchupbackend.entity.Comment;
import com.example.matchup.matchupbackend.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFeed(Feed feed);
}
