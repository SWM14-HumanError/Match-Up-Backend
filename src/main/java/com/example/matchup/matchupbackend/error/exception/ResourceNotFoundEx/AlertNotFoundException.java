package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

public class AlertNotFoundException extends ResourceNotFoundException{
    public AlertNotFoundException() {
        super(ErrorCode.ALERT_NOT_FOUND);
    }

    public AlertNotFoundException(String detailInfo) {
        super(ErrorCode.ALERT_NOT_FOUND, detailInfo);
    }
}
