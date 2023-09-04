package com.example.matchup.matchupbackend.error.exception.DuplicateRecruitEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

import static com.example.matchup.matchupbackend.error.ErrorCode.TEAM_USER_RECRUIT_ERROR;

public class DuplicateTeamRecruitException extends DuplicateRecruitException {
    private Long recruitUser;
    private Long recruitTeam;

    public DuplicateTeamRecruitException(Long recruitUser, Long recruitTeam) {
        super(TEAM_USER_RECRUIT_ERROR);
        this.recruitUser = recruitUser;
        this.recruitTeam = recruitTeam;
    }
}
