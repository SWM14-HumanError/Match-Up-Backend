package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.RECRUIT_INFO_NOT_FOUND;

@Getter
public class RecruitNotFoundException extends ResourceNotFoundException{
    public RecruitNotFoundException() {
        super(RECRUIT_INFO_NOT_FOUND);
    }

    public RecruitNotFoundException(String detailInfo) {
        super(RECRUIT_INFO_NOT_FOUND, detailInfo);
    }
}
