package com.example.matchup.matchupbackend.dto.request.teamuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefuseFormRequest {
    @NotNull(message = "팀에 지원했던 유저 ID는 필수 값입니다.")
    private Long recruitUserID;
    @NotBlank(message = "팀에 지원했던 역할 입력은 필수 입니다")
    private String role;
    @NotBlank(message = "거절 사유 입력은 필수 입니다")
    private String refuseReason;
}
