package com.example.matchup.matchupbackend.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ApprovedMember extends Member {
    private Long count;
}
