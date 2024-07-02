package com.example.matchup.matchupbackend.dto.request.team;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
public class TeamSearchRequest {
    @NotNull(message = "null이 반환 되었습니다 -> 기업 프로젝트 = 0, 개인 프로젝트 = 1 둘중 하나를 선택하세요")
    @Range(max = 1L, message = "0, 1 외에 다른 값이 오면 안됩니다 (0 = 기업 프로젝트, 1 = 개인 프로젝트)")
    private Long type;
    private String category;
    private String search;
    private int page;
    private int size;

    @Builder
    public TeamSearchRequest(Long type, String category, String search, int page, int size) {
        this.type = type;
        this.category = category;
        this.search = search;
        this.page = page;
        this.size = size;
    }
}
