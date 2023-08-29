package com.example.matchup.matchupbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
public class TeamType {
    @NotNull(message = "팀 구분은 필수입니다")
    @Range(max=1, message = "0(Project) or 1(Study)로만 입력하세요")
    private Long teamType; //project or study
    @NotBlank(message = "세부타입은 필수값입니다")
    private String detailType; //세부 타입 설정
    @Builder
    public TeamType(Long teamType, String detailType) {
        this.teamType = teamType;
        this.detailType = detailType;
    }
}
