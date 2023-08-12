package com.example.matchup.matchupbackend.global.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAccessTokenResponseDto {

    private String accessToken;
}
