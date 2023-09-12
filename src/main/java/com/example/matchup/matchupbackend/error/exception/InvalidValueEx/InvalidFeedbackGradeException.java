package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

public class InvalidFeedbackGradeException extends InvalidValueException{
    public InvalidFeedbackGradeException() {
        super(ErrorCode.INVALID_FEEDBACK_GRADE);
    }

    public InvalidFeedbackGradeException(String requestValue) {
        super(ErrorCode.INVALID_FEEDBACK_GRADE, requestValue);
    }
}
