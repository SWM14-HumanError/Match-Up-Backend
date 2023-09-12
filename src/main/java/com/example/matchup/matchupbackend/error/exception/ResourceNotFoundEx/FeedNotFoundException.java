package com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx;

import static com.example.matchup.matchupbackend.error.ErrorCode.FEED_NOT_FOUND;

public class FeedNotFoundException extends ResourceNotFoundException{
    public FeedNotFoundException() {
        super(FEED_NOT_FOUND);
    }

    public FeedNotFoundException(String detailInfo) {
        super(FEED_NOT_FOUND, detailInfo);
    }
}
