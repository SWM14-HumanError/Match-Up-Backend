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
    EXPIRED_RESOURCE_ACCESS(HttpStatus.UNAUTHORIZED, "G-006", "만료된 토큰입니다."),
    TOKEN_REFRESH_NOT_PERMIT(HttpStatus.NOT_FOUND, "G-007", "갱신되었거나 등록되지 않은 refresh 토큰입니다."),
    NOT_PERMITTED(HttpStatus.BAD_REQUEST, "G-008", "적절하지 않은 접근입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "G-009", "존재하지 않은 자원에 대한 접근입니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "T-S-001", "게시글이 존재하지 않습니다."),
    LEADER_ONLY_MODIFY(HttpStatus.UNAUTHORIZED, "T-S-002", "팀장만 접근이 가능 합니다."),
    MAX_MEMBER_ERROR(HttpStatus.BAD_REQUEST, "T-S-003", "현재 팀원보다 최대 팀원 수를 높게 정하세요."),
    TEAM_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "T-S-004", "팀 세부 정보가 존재하지 않습니다."),
    TEAM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "TU-S-001", "팀원 정보가 존재하지 않습니다."),
    TEAM_POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "TU-S-002", "팀 구성 정보가 존재하지 않습니다."),
    TEAM_USER_RECRUIT_ERROR(HttpStatus.BAD_REQUEST, "TU-S-003", "이미 지원한 팀입니다."),
    TEAM_USER_ACCEPT_ERROR(HttpStatus.BAD_REQUEST, "TU-S-004", "이미 팀원입니다."),
    TEAM_USER_FULL_ERROR(HttpStatus.BAD_REQUEST, "TU-S-005", "이미 팀원이 꽉 찼습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-S-001", "유저 정보가 존재하지 않습니다."),
    USER_DELETE_ERROR(HttpStatus.BAD_REQUEST, "U-S-002", "유저 탈퇴를 할 수 없습니다."),
    USER_NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "UP-S-001", "유저 닉네임이 중복되거나 제한된 글자 수를 초과했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F-S-001", "파일을 저장 중 오류가 발생했습니다."),
    FILE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "F-S-002", "지원하지 않는 파일 확장자 입니다."),
    MAX_FILE_SIZE_ERROR(HttpStatus.BAD_REQUEST, "F-S-003", "너무 큰 파일을 업로드 하였습니다."),
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "FE-S-001", "피드 정보가 존재하지 않습니다."),
    FEED_TEAM_LIKE_ALREADY(HttpStatus.BAD_REQUEST, "FE-S-002", "이미 좋아요를 누른 피드입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C-S-001", "찾을 수 없는 댓글입니다."),
    INVALID_FEEDBACK_GRADE(HttpStatus.BAD_REQUEST, "FB-E-001", "피드백 점수는 GREAT, NORMAL, BAD만 가능합니다."),
    TEAM_FEEDBACK_AVAILABLE(HttpStatus.BAD_REQUEST, "FB-S-001", "잘못된 피드백 입니다."),
    ALERT_NOT_FOUND(HttpStatus.NOT_FOUND, "A-S-001", "알림 정보가 존재하지 않습니다."),
    ALERT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "A-S-002", "이미 삭제된 알림입니다."),
    RECRUIT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "TU-S-005", "지원 정보가 존재하지 않습니다."),
    REFUSE_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "TU-S-006", "거절 정보가 존재하지 않습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "L-S-001", "좋아요를 누른 기록이 없습니다."),
    INVALID_LIKE(HttpStatus.BAD_REQUEST, "L-S-002", "잘못된 좋아요입니다."),
    INVALID_CHATTING_ROOM(HttpStatus.BAD_REQUEST, "CHAT-001", "잘못된 채팅방입니다."),
    ADMIN_ONLY(HttpStatus.UNAUTHORIZED, "ADMIN-001", "ADMIN 만 가능 합니다."),
    INVALID_ENTERPRISE_VERIFY(HttpStatus.BAD_REQUEST, "E-S-001", "잘못된 기업 인증 상태 변경 요청 입니다.");


    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
