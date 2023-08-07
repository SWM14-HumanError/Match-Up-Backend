package com.example.matchup.matchupbackend.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class Member {
    private String role;
    private List<String> stacks;
    private Long maxCount;
}
