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
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "T-S-001", "게시글이 존재하지 않습니다."),
    LEADER_ONLY_MODIFY(HttpStatus.UNAUTHORIZED, "T-S-002", "팀장만 접근이 가능 합니다."),
    MAX_MEMBER_ERROR(HttpStatus.BAD_REQUEST, "T-S-003", "현재 팀원보다 최대 팀원 수를 높게 정하세요."),
    TEAM_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "T-S-004", "팀 세부 정보가 존재하지 않습니다."),
    TEAM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "TU-S-001", "팀원 정보가 존재하지 않습니다."),
    TEAM_POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "TU-S-002", "팀 구성 정보가 존재하지 않습니다."),
    TEAM_USER_RECRUIT_ERROR(HttpStatus.BAD_REQUEST, "TU-S-003", "이미 지원한 팀입니다."),
    TEAM_USER_ACCEPT_ERROR(HttpStatus.BAD_REQUEST, "TU-S-004", "이미 팀원입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-S-001", "유저 정보가 존재하지 않습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F-S-001", "파일을 저장 중 오류가 발생했습니다."),
    FILE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "F-S-002", "지원하지 않는 파일 확장자 입니다."),
    MAX_FILE_SIZE_ERROR(HttpStatus.BAD_REQUEST, "F-S-003", "너무 큰 파일을 업로드 하였습니다."),
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "FE-S-001", "피드 정보가 존재하지 않습니다."),
    INVALID_FEEDBACK_GRADE(HttpStatus.BAD_REQUEST, "FB-E-001", "피드백 점수는 GREAT, NORMAL, BAD만 가능합니다."),
    TEAM_FEEDBACK_AVAILABLE(HttpStatus.BAD_REQUEST, "FB-S-001", "잘못된 피드백 입니다."),;

    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
