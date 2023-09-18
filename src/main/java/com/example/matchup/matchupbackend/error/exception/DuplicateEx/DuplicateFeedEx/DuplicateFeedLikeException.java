package com.example.matchup.matchupbackend.error.exception.DuplicateEx.DuplicateFeedEx;

import com.example.matchup.matchupbackend.error.exception.CustomException;
import lombok.Getter;

import static com.example.matchup.matchupbackend.error.ErrorCode.FEED_LIKE_ALREADY;

@Getter
public class DuplicateFeedLikeException  extends CustomException {

    String resource;

    public DuplicateFeedLikeException(String resource) {
        super(FEED_LIKE_ALREADY);
        this.resource = resource;
    }
}
