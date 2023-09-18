package com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateRecruitEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateAcceptTeamUserException extends DuplicateRecruitException {
    public DuplicateAcceptTeamUserException() {
        super(ErrorCode.TEAM_USER_ACCEPT_ERROR);
    }

    public DuplicateAcceptTeamUserException(Long recruitUserID, Long recruitTeamID) {
        super(ErrorCode.TEAM_USER_ACCEPT_ERROR, recruitUserID, recruitTeamID);
    }

    public String getErrorDetail() {
        String detailInfo = "userID: " + recruitUserID + " already accepted teamID: " + recruitTeamID;
        return detailInfo;
    }
}
