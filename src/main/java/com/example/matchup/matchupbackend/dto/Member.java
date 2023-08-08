package com.example.matchup.matchupbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class Member {
    private String role;
    private List<String> stacks;
    private Long maxCount;
}
