package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.LIKE_NOT_FOUND;

@Getter
public class LikeNotFoundException extends ResourceNotFoundException {
    private Long likeGiverId;

    public LikeNotFoundException(Long likeGiverId) {
        super(LIKE_NOT_FOUND);
        this.likeGiverId = likeGiverId;
    }

    public LikeNotFoundException(String detailInfo, Long likeGiverId) {
        super(LIKE_NOT_FOUND, detailInfo);
        this.likeGiverId = likeGiverId;
    }
}
