package com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.TEAM_USER_RECRUIT_ERROR;

@Getter
public class DuplicateTeamRecruitException extends DuplicateRecruitException {
    private Long recruitUserID;
    private Long recruitTeamID;

    public DuplicateTeamRecruitException(Long recruitUserID, Long recruitTeamID) {
        super(TEAM_USER_RECRUIT_ERROR);
        this.recruitUserID = recruitUserID;
        this.recruitTeamID = recruitTeamID;
    }

    public String getErrorDetail() {
        String detailInfo = "userID: " + recruitUserID + " already apply teamID: " + recruitTeamID;
        return detailInfo;
    }
}