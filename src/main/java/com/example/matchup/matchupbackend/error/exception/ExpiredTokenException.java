package com.example.matchup.matchupbackend.error.exception;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.EXPIRED_RESOURCE_ACCESS;

@Getter
public class ExpiredTokenException extends CustomException {

    private String resource;

    public ExpiredTokenException() {
        super(EXPIRED_RESOURCE_ACCESS);
        this.resource = "액세스 토큰이 만료되었습니다.";
    }

    public ExpiredTokenException(String resource) {
        super(EXPIRED_RESOURCE_ACCESS);
        this.resource = resource;
    }
}