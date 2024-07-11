package com.example.matchup.matchupbackend.dto.request.feed.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedCommentCreateOrUpdateRequest {

    @NotBlank(message = "피드의 댓글은 비어있을 수 없습니다.")
    @Size(max = 50, message = "피드의 댓글은 50글자를 넘길 수 없습니다.")
    private String content;
}
