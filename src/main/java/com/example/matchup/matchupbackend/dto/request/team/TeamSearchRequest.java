package com.example.matchup.matchupbackend.dto.request.team;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class TeamSearchRequest {
    @NotNull(message = "null이 반환 되었습니다 -> 프로젝트 = 0, 스터디 = 1 둘중 하나를 선택하세요")
    @Range(max = 1L, message = "0, 1 외에 다른 값이 오면 안됩니다 (0 = 프로젝트, 1 = 스터디)")
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
