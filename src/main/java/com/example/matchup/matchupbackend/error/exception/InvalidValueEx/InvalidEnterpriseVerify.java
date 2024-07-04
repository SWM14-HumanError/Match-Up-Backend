package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.INVALID_ENTERPRISE_VERIFY;

@Getter
public class InvalidEnterpriseVerify extends InvalidValueException{
    public InvalidEnterpriseVerify() {
        super(INVALID_ENTERPRISE_VERIFY);
    }

    public InvalidEnterpriseVerify(String requestValue) {
        super(INVALID_ENTERPRISE_VERIFY, requestValue);
    }
}
