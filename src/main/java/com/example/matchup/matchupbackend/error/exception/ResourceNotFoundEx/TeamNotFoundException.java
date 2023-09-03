package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class TeamNotFoundException extends ResourceNotFoundException {
    public TeamNotFoundException() {
        super(ErrorCode.TEAM_NOT_FOUND);
    }

    public TeamNotFoundException(String detailInfo) {
        super(ErrorCode.TEAM_NOT_FOUND, detailInfo);
    }
}
