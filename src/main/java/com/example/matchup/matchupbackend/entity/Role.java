package com.example.matchup.matchupbackend.entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "일반 사용자"),
    MENTOR("ROLE_MENTOR", "멘토 사용자"),
    ENTERPRISE("ROLE_ENTERPRISE", "기업 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
