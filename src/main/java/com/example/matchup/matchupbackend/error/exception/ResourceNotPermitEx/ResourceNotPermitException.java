package com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

@Getter
public class ResourceNotPermitException extends CustomException {
    private String resource;

    public ResourceNotPermitException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotPermitException(ErrorCode errorCode, String resource) {
        super(errorCode);
        this.resource = resource;
    }
}
