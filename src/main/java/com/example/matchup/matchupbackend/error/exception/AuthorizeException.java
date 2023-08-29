package com.example.matchup.matchupbackend.error.exception;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorizeException extends CustomException {
    private String resource;

    public AuthorizeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthorizeException(ErrorCode errorCode, String resource) {
        super(errorCode);
        this.resource = resource;
    }
}
