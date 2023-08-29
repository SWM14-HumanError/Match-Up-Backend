package com.example.matchup.matchupbackend.error.exception;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    public ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
