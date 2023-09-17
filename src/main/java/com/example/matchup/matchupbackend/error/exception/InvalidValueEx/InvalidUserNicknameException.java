package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidUserNicknameException extends InvalidValueException {

    public InvalidUserNicknameException() {
        super(ErrorCode.USER_NICKNAME_DUPLICATE);
    }
}
