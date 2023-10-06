package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

import static com.example.matchup.matchupbackend.error.ErrorCode.INVALID_LIKE;

public class InvalidLikeException extends InvalidValueException{
    private String detailMessage;
    public InvalidLikeException() {
        super(INVALID_LIKE);
    }

    public InvalidLikeException(String requestValue) {
        super(INVALID_LIKE, requestValue);
    }

    public InvalidLikeException(ErrorCode errorCode, String requestValue, String detailMessage) {
        super(INVALID_LIKE, requestValue);
        this.detailMessage = detailMessage;
    }
}
