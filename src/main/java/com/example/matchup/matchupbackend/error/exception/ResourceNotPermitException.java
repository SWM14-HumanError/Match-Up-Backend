package com.example.matchup.matchupbackend.error.exception;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class ResourceNotPermitException extends CustomException {

    public ResourceNotPermitException(ErrorCode errorCode) {
        super(errorCode);
    }

}
