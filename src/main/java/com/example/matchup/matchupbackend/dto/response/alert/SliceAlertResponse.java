package com.example.matchup.matchupbackend.dto.response.alert;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SliceAlertResponse {
    private List<AlertResponse> alertResponseList;
    private int size;
    private boolean hasNextSlice;
}
