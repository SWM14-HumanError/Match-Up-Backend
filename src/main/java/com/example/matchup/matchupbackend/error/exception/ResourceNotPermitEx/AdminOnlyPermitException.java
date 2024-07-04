package com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class AdminOnlyPermitException extends ResourceNotPermitException{
    public AdminOnlyPermitException() {
        super(ErrorCode.ADMIN_ONLY);
    }

    public AdminOnlyPermitException(String resource) {
        super(ErrorCode.ADMIN_ONLY, resource);
    }
}
