package com.example.matchup.matchupbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Range;

@Data
public class TeamSearchRequest {
    @Range(max = 1L)
    private Long type;
    private String category;
    private String search;
    @NotNull
    private int page;
    @NotNull
    private int size;
}
