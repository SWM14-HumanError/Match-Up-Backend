package com.example.matchup.matchupbackend.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLikeResponse {
    private Boolean check;
    private Long totalLike;
}
