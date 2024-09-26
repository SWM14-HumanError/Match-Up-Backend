package com.example.matchup.matchupbackend.dto.request.mentoring;

import com.example.matchup.matchupbackend.entity.Career;
import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.error.annotation.Enum;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplyVerifyMentorRequest {

    private String imageName;

    private String imageBase64;

    @Enum(enumClass = RoleType.class, message = "PLAN, UI_UX, FE, BE, APP, GAME, AI, ETC, LEADER")
    private String roleType;

    @Enum(enumClass = Career.class, message = "주니어, 미들, 시니어")
    private String career;

    @Size(max = 700, message = "멘토 인증의 글은 700글자를 넘길 수 없습니다.")
    private String content;

    private String link;

    public RoleType getRoleType() {
        return this.roleType != null ? RoleType.valueOf(this.roleType) : null;
    }

    public Career getCareer() {
        return this.career != null ? Career.valueOf(this.career) : null;
    }
}
