package com.example.matchup.matchupbackend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Career {

    주니어("1~3년"), 미들("4~8년"), 시니어("9년 이상");

    private final String text;
}
