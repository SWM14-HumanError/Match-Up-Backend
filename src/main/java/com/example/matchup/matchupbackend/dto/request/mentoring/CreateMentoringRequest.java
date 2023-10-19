package com.example.matchup.matchupbackend.dto.request.mentoring;

import com.example.matchup.matchupbackend.entity.Career;
import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMentoringRequest {

    @NotBlank(message = "멘토링 제목은 비어있을 수 없습니다.")
    @Size(max = 50, message = "멘토링 제목은 50글자를 넘길 수 없습니다.")
    private String title;

    @NotBlank(message = "멘토링 내용은 비어있을 수 없습니다.")
    @Size(max = 700, message = "멘토링 내용은 700글자를 넘길 수 없습니다.")
    private String content;

    @NotBlank(message = "멘토링 직무는 필수 값입니다")
    @Enum(enumClass = RoleType.class, message = "PLAN, UI_UX, FE, BE, APP, GAME, AI, ETC, LEADER")
    private String roleType;

    @NotBlank(message = "멘토링 경력은 필수 값입니다")
    @Enum(enumClass = Career.class, message = "주니어, 미들, 시니어")
    private String career;

    public RoleType getRoleType() {
        return RoleType.valueOf(this.roleType);
    }

    public Career getCareer() {
        return Career.valueOf(this.career);
    }
}
