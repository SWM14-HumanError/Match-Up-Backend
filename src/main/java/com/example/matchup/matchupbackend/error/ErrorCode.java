package com.example.matchup.matchupbackend.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    SEVER_GLOBAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G-001", "서버에 알 수 없는 오류가 발생했습니다."),
    INVALID_REQUEST_ENCODE(HttpStatus.BAD_REQUEST, "G-002", "JSON 형식 요청이 아닙니다."),
    REQUEST_FIELD_ERROR(HttpStatus.BAD_REQUEST, "G-003", "입력 필드에 오류가 있습니다."),
    UNAUTHORIZED_RESOURCE_ACCESS(HttpStatus.UNAUTHORIZED , "G-004", "인가되지 않은 자원으로 접근하였습니다.");

    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
