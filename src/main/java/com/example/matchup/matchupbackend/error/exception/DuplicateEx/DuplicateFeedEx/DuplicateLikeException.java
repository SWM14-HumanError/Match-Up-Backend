package com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateFeedEx;

import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.FEED_TEAM_LIKE_ALREADY;

@Getter
public class DuplicateLikeException extends CustomException {

    String resource;

    public DuplicateLikeException(String resource) {
        super(FEED_TEAM_LIKE_ALREADY);
        this.resource = resource;
    }
}
