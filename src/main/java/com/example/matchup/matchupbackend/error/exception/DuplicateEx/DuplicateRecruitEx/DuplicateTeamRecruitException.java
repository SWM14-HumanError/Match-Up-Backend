package com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateRecruitEx;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.TEAM_USER_RECRUIT_ERROR;

@Getter
public class DuplicateTeamRecruitException extends DuplicateRecruitException {

    public DuplicateTeamRecruitException(Long recruitUserID, Long recruitTeamID) {
        super(TEAM_USER_RECRUIT_ERROR, recruitUserID, recruitTeamID);
    }

    public DuplicateTeamRecruitException() {
        super(TEAM_USER_RECRUIT_ERROR);
    }

    public String getErrorDetail() {
        String detailInfo = "userID: " + recruitUserID + " already apply teamID: " + recruitTeamID;
        return detailInfo;
    }
}