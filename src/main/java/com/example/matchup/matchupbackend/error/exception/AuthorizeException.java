package com.example.matchup.matchupbackend.error.exception;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorizeException extends CustomException {
    private String resource;

    public AuthorizeException() {
        super(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
    }

    public AuthorizeException(String resource) {
        super(ErrorCode.UNAUTHORIZED_RESOURCE_ACCESS);
        this.resource = resource;
    }
}
