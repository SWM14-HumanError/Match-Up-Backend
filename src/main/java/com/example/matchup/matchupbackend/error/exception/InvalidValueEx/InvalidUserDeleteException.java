package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

public class InvalidUserDeleteException extends InvalidValueException{

    public InvalidUserDeleteException() {
        super(ErrorCode.USER_DELETE_ERROR);
    }

    public InvalidUserDeleteException(String requestValue) {
        super(ErrorCode.USER_DELETE_ERROR, requestValue);
    }
}
