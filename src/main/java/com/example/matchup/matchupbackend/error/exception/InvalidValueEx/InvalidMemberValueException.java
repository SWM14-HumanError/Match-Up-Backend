package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidMemberValueException extends InvalidValueException {
    public InvalidMemberValueException() {
        super(ErrorCode.MAX_MEMBER_ERROR);
    }

    public InvalidMemberValueException(Long requestValue) {
        super(ErrorCode.MAX_MEMBER_ERROR, requestValue);
    }
}
