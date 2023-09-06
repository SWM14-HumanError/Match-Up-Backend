package com.example.matchup.matchupbackend.dto.feed;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FeedSliceResponseDto {

    private List<FeedSearchResponseDto> feedSearchResponsDtos;
    private int size;
    private Boolean hasNextSlice;
}
