package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import static com.example.matchup.matchupbackend.error.ErrorCode.TEAM_USER_FULL_ERROR;

public class InvalidRecruitException extends InvalidValueException {

    public InvalidRecruitException() {
        super(TEAM_USER_FULL_ERROR);
    }

    public InvalidRecruitException(String requestValue) {
        super(TEAM_USER_FULL_ERROR, requestValue);
    }
}
