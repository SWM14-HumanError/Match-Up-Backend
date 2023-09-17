package com.example.matchup.matchupbackend.dto.response.alert;

import lombok.Data;

import java.util.List;

@Data
public class SliceAlertResponse {
    private List<AlertResponse> alertResponseList;
    private int size;
    private Boolean hasNextSlice;
}
