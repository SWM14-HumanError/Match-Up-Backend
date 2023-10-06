package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import static com.example.matchup.matchupbackend.error.ErrorCode.INVALID_LIKE;

public class InvalidLikeException extends InvalidValueException{
    private String detailMessage;
    public InvalidLikeException() {
        super(INVALID_LIKE);
    }

    public InvalidLikeException(String requestValue) {
        super(INVALID_LIKE, requestValue);
    }
}
