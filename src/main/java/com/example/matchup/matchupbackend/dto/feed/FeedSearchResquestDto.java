package com.example.matchup.matchupbackend.dto.feed;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedSearchResquestDto {

    @NotNull
    private SearchType searchType; // 작성자 혹은 피드 제목
    private String searchValue;
}

