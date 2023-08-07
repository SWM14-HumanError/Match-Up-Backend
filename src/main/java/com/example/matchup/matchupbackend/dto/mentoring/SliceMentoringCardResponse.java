package com.example.matchup.matchupbackend.dto.mentoring;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SliceMentoringCardResponse {
    private List<MentoringCardResponse> mentoringCardResponses;
    private int size;
    private Boolean hasNextSlice;
}
