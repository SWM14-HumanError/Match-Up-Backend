package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

public class RefuseInfoNotFoundException extends ResourceNotFoundException{
    public RefuseInfoNotFoundException() {
        super(ErrorCode.REFUSE_INFO_NOT_FOUND);
    }

    public RefuseInfoNotFoundException(String detailInfo) {
        super(ErrorCode.REFUSE_INFO_NOT_FOUND, detailInfo);
    }
}
