package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;
import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends CustomException {
    private String detailInfo;

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String detailInfo) {
        super(errorCode);
        this.detailInfo = detailInfo;
    }
}
