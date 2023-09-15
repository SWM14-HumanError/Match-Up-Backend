package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.TEAM_FEEDBACK_AVAILABLE;

@Getter
public class InvalidFeedbackException extends InvalidValueException {
    private String detailMessage;

    public InvalidFeedbackException() {
        super(TEAM_FEEDBACK_AVAILABLE);
    }

    public InvalidFeedbackException(String detailMessage) {
        super(TEAM_FEEDBACK_AVAILABLE);
        this.detailMessage = detailMessage;
    }

    public InvalidFeedbackException(String requestValue, String detailMessage) {
        super(TEAM_FEEDBACK_AVAILABLE, requestValue);
        this.detailMessage = detailMessage;
    }
}
