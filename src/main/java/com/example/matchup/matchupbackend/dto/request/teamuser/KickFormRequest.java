package com.example.matchup.matchupbackend.dto.request.teamuser;

import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KickFormRequest {
    @NotNull(message = "팀에서 방출하는 유저 ID는 필수 값입니다.")
    private Long kickUserID;
    @NotBlank(message = "지원 역할 입력은 필수 입니다")
    @Enum(enumClass = RoleType.class, message = "PLAN, UI_UX, FE, BE, APP, GAME, AI, ETC, LEADER")
    private String role;
    @NotBlank(message = "방출 사유 입력은 필수 입니다")
    private String refuseReason;

    public RoleType getRole() {
        return RoleType.valueOf(this.role);
    }
}
