package com.example.matchup.matchupbackend.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * T-S : team service에서 던지는 예외
 * T-C : team Controller에서 던지는 예외
 * G : Global 예외
 */
@Getter
public enum ErrorCode {
    SEVER_GLOBAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G-001", "서버에 알 수 없는 오류가 발생했습니다."),
    INVALID_REQUEST_ENCODE(HttpStatus.BAD_REQUEST, "G-002", "JSON 형식 요청이 아닙니다."),
    REQUEST_FIELD_ERROR(HttpStatus.BAD_REQUEST, "G-003", "입력 필드에 오류가 있습니다."),
    UNAUTHORIZED_RESOURCE_ACCESS(HttpStatus.UNAUTHORIZED, "G-004", "토큰이 유효하지 않습니다."),
    MISSING_REQUEST_HEADER(HttpStatus.UNAUTHORIZED, "G-005", "header 값이 없습니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "T-S-001", "게시글이 존재하지 않습니다.");


    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
