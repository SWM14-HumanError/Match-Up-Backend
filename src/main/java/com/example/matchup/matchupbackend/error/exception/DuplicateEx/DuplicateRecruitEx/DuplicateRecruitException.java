package com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateRecruitEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

@Getter
public class DuplicateRecruitException extends CustomException {
    protected Long recruitUserID;
    protected Long recruitTeamID;

    public DuplicateRecruitException(ErrorCode errorCode, Long recruitUserID, Long recruitTeamID) {
        super(errorCode);
        this.recruitUserID = recruitUserID;
        this.recruitTeamID = recruitTeamID;
    }

    public DuplicateRecruitException(ErrorCode errorCode) {
        super(errorCode);
    }
}
