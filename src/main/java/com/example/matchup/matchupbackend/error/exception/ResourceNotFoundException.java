package com.example.matchup.matchupbackend.error.exception;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends CustomException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
