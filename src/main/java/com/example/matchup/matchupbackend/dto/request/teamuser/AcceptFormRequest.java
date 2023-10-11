package com.example.matchup.matchupbackend.dto.request.teamuser;

import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptFormRequest {
    @NotNull(message = "팀에 지원하는 유저 ID는 필수 값입니다.")
    private Long recruitUserID;
    @NotBlank(message = "지원 역할 입력은 필수 입니다")
    private RoleType role;
}
