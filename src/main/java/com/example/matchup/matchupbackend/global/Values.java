package com.example.matchup.matchupbackend.global;

import lombok.Getter;

@Getter
public enum Values {
    FEEDBACK_PERIOD(7, "피드백 기간");;

    private Integer value;
    private String text;

    Values(Integer value, String text) {
        this.value = value;
        this.text = text;
    }
}
