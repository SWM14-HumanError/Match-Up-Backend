package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class TeamUserNotFoundException extends ResourceNotFoundException {
    public TeamUserNotFoundException() {
        super(ErrorCode.TEAM_USER_NOT_FOUND);
    }

    public TeamUserNotFoundException(String detailInfo) {
        super(ErrorCode.TEAM_USER_NOT_FOUND, detailInfo);
    }
}
