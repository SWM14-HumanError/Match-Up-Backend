package com.example.matchup.matchupbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class TeamSearchRequest {
    @Range(max = 1L, message = "0, 1 외에 다른 값이 오면 안됩니다 (0 = 프로젝트, 1 = 스터디)")
    private Long type;
    private String category;
    private String search;
    @NotNull(message = "몇번째 페이지인지 입력하세요")
    private int page;
    @NotNull(message = "한 페이지에 몇개씩 보여줄건지 정하세요")
    private int size;
}
