package com.example.matchup.matchupbackend.dto.response.feed;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FeedSliceResponse {

    private List<FeedSearchResponse> feedSearchResponses;
    private int size;
    private Boolean hasNextSlice;
}
