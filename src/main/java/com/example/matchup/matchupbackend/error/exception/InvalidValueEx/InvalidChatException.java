package com.example.matchup.matchupbackend.error.exception.InvalidValueEx;

import static com.example.matchup.matchupbackend.error.ErrorCode.INVALID_CHATTING_ROOM;

public class InvalidChatException extends InvalidValueException {

    private String extraMessage;

    public InvalidChatException() {
        super(INVALID_CHATTING_ROOM);
    }

    public InvalidChatException(String requestValue) {
        super(INVALID_CHATTING_ROOM, requestValue);
    }

    public InvalidChatException(String requestValue, String extraMessage) {
        super(INVALID_CHATTING_ROOM, requestValue);
        this.extraMessage = extraMessage;
    }
}
