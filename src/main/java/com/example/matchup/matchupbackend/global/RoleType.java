package com.example.matchup.matchupbackend.global;

import lombok.Getter;

@Getter
public enum RoleType {
    PLAN("기획"), UI_UX("UI/UX"), FE("프론트엔드"), BE("백엔드"), APP("앱"), GAME("게임"), AI("AI"), ETC("기타");

    private String role;

    RoleType(String role) {
        this.role = role;
    }
}
