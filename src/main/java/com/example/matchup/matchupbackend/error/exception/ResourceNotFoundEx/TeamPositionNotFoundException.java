package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class TeamPositionNotFoundException extends ResourceNotFoundException{
    public TeamPositionNotFoundException() {
        super(ErrorCode.TEAM_POSITION_NOT_FOUND);
    }

    public TeamPositionNotFoundException(String detailInfo) {
        super(ErrorCode.TEAM_POSITION_NOT_FOUND, detailInfo);
    }
}
