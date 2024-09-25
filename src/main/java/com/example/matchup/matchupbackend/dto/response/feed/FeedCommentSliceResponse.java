package com.example.matchup.matchupbackend.dto.response.feed;

import lombok.Data;

import java.util.List;

@Data
public class FeedCommentSliceResponse {

    private List<FeedCommentResponse> comments;
    private int size;
    private Boolean hasNextSlice;
}
