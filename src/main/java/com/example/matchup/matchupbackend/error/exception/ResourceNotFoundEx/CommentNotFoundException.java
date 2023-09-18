package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.COMMENT_NOT_FOUND;

@Getter
public class CommentNotFoundException extends ResourceNotFoundException{

    String detailInfo;

    public CommentNotFoundException(String detailInfo) {
        super(COMMENT_NOT_FOUND);
        this.detailInfo = detailInfo;
    }
}
