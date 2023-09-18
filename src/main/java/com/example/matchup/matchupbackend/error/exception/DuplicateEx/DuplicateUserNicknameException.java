package com.example.matchup.matchupbackend.error.exception.DuplicateEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

@Getter
public class DuplicateUserNicknameException extends CustomException {

    public DuplicateUserNicknameException() {
        super(ErrorCode.USER_NICKNAME_DUPLICATE);
    }
}
