package com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class LeaderOnlyPermitException extends ResourceNotPermitException {

    public LeaderOnlyPermitException(String resource) {
        super(ErrorCode.LEADER_ONLY_MODIFY, resource);
    }

    public LeaderOnlyPermitException() {
        super(ErrorCode.LEADER_ONLY_MODIFY);
    }
}
