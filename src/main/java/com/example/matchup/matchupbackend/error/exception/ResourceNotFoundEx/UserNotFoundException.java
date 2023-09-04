package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import com.example.matchup.matchupbackend.error.ErrorCode;

import static com.example.matchup.matchupbackend.error.ErrorCode.USER_NOT_FOUND;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }

    public UserNotFoundException(String detailInfo) {
        super(USER_NOT_FOUND, detailInfo);
    }
}
