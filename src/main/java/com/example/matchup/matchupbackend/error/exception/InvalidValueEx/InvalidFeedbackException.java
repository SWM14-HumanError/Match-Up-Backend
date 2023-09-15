package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.exception.CustomException;

import static com.example.matchup.matchupbackend.error.ErrorCode.TEAM_FEEDBACK_AVAILABLE;

public class InvalidFeedbackException extends CustomException {
    private String detailMessage;

    public InvalidFeedbackException() {
        super(TEAM_FEEDBACK_AVAILABLE);
    }

    public InvalidFeedbackException(String detailMessage) {
        super(TEAM_FEEDBACK_AVAILABLE);
        this.detailMessage = detailMessage;
    }
}
