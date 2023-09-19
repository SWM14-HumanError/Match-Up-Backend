package com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.TOKEN_REFRESH_NOT_PERMIT;

@Getter
public class TokenRefreshNotPermitException extends ResourceNotPermitException{

    public TokenRefreshNotPermitException() {
        super(TOKEN_REFRESH_NOT_PERMIT);
    }
}
