package com.example.matchup.matchupbackend.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {
    SEVER_GLOBAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR , "GlobalError-001", "서버에 알 수 없는 오류가 발생했습니다.");



    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
