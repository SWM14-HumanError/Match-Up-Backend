package com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.CustomException;

public class DuplicateRecruitException extends CustomException {

    public DuplicateRecruitException(ErrorCode errorCode) {
        super(errorCode);
    }
}
