package com.example.matchup.matchupbackend.error.exception;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidValueException extends CustomException {
    private Long requestValue;

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidValueException(ErrorCode errorCode, Long requestValue) {
        super(errorCode);
        this.requestValue = requestValue;
    }
}
