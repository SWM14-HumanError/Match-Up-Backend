package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class Member {

    @NotBlank(message = "팀원의 역할은 필수 값입니다")
    @Enum(enumClass = RoleType.class, message = "PLAN, UI_UX, FE, BE, APP, GAME, AI, ETC, LEADER")
    private String role;

    @NotNull(message = "팀원의 기술 스택은 필수 값입니다") // 빈 리스트는 가능
    private List<String> stacks;

    @NotNull(message = "역할의 최대 모집 인원은 필수 값입니다")
    @Range(min = 1, max = 10, message = "역할별 모집 가능 팀원은 1~10명 사이입니다")
    private Long maxCount;

    public Member(RoleType role, List<String> stacks, Long maxCount) {
        this.role = role.toString();
        this.stacks = stacks;
        this.maxCount = maxCount;
    }

    public RoleType getRole() {
        return RoleType.valueOf(role);
    }
}
