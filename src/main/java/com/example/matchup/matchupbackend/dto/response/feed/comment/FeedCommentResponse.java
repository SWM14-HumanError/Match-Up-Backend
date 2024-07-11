package com.example.matchup.matchupbackend.dto.response.feed.comment;

import com.example.matchup.matchupbackend.entity.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedCommentResponse {

    private String commentWriter;
    private Long commentId;
    private Long userId;
    private LocalDate createdAt;
    private String content;

    @Builder
    public FeedCommentResponse(Comment comment) {
        this.commentWriter = comment.getUser().getNickname();
        this.commentId = comment.getId();
        this.userId = comment.getUser().getId();
        this.createdAt = comment.getCreateTime().toLocalDate();
        this.content = comment.getContent();
    }
}
