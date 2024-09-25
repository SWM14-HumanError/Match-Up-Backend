package com.example.matchup.matchupbackend.dto.request.teamuser;

import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.error.annotation.Enum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecruitFormRequest {

    @NotBlank(message = "역할 입력은 필수 입니다")
    @Enum(enumClass = RoleType.class, message = "PLAN, UI_UX, FE, BE, APP, GAME, AI, ETC, LEADER")
    private String role;

    @NotBlank(message = "자기소개 입력은 필수 입니다")
    private String content;

    public RoleType getRole() {
        return RoleType.valueOf(role);
    }
}
