package com.example.matchup.matchupbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class Member {
    private String role;
    private List<String> stacks;
    private Long count;
}
