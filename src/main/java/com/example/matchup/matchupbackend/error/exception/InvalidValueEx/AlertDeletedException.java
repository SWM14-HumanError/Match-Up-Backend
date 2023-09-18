package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

public class AlertDeletedException extends InvalidValueException{
    public AlertDeletedException() {
        super(ErrorCode.ALERT_ALREADY_DELETED);
    }

    public AlertDeletedException(String requestValue) {
        super(ErrorCode.ALERT_ALREADY_DELETED, requestValue);
    }
}
