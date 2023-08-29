package com.example.matchup.matchupbackend.error;

import lombok.Data;

@Data
public class ErrorResult {
    private String code;
    private String message;
    private Object messageExtra;

    private ErrorResult(String code, String message, Object messageExtra) {
        this.code = code;
        this.message = message;
        this.messageExtra = messageExtra;
    }

    public static ErrorResult of(ErrorCode errorCode) {
        return ErrorResult.of(errorCode, null);
    }

    public static ErrorResult of(ErrorCode errorCode, Object extraInfo) {
        return new ErrorResult(errorCode.getCode(), errorCode.getMessage(), extraInfo);
    }
}
