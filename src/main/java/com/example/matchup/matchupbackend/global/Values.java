package com.example.matchup.matchupbackend.global;

import lombok.Getter;

@Getter
public enum Values {
    FEEDBACK_PERIOD("피드백 기간", 7);
    private String text; // enum 설명
    private Integer value;

    Values(String text, Integer value) {
        this.text = text;
        this.value = value;
    }
}
