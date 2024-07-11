package com.example.matchup.matchupbackend.dto.response.user;

import com.example.matchup.matchupbackend.dto.UserCardResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
public class SliceUserCardResponse {
    private List<UserCardResponse> userCardResponses;
    private int size;
    private Boolean hasNextSlice;
    @Builder
    public SliceUserCardResponse(List<UserCardResponse> userCardResponses, int size, Boolean hasNextSlice) {
        this.userCardResponses = userCardResponses;
        this.size = size;
        this.hasNextSlice = hasNextSlice;
    }
}
