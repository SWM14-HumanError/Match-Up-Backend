package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import static com.example.matchup.matchupbackend.error.ErrorCode.LIKE_CANT_FOR_ME;

public class InvalidLikeException extends InvalidValueException{
    public InvalidLikeException() {
        super(LIKE_CANT_FOR_ME);
    }

    public InvalidLikeException(String requestValue) {
        super(LIKE_CANT_FOR_ME, requestValue);
    }
}
