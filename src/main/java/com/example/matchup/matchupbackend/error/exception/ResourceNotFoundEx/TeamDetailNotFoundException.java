package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.TEAM_DETAIL_NOT_FOUND;

@Getter
public class TeamDetailNotFoundException extends ResourceNotFoundException {
    public TeamDetailNotFoundException() {
        super(TEAM_DETAIL_NOT_FOUND);
    }

    public TeamDetailNotFoundException(String detailInfo) {
        super(TEAM_DETAIL_NOT_FOUND, detailInfo);
    }
}
