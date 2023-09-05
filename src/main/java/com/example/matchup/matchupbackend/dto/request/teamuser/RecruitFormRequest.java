package com.example.matchup.matchupbackend.dto.request.teamuser;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecruitFormRequest {
    @NotBlank(message = "역할 입력은 필수 입니다")
    private String role;
    @NotBlank(message = "자기소개 입력은 필수 입니다")
    private String content;
}