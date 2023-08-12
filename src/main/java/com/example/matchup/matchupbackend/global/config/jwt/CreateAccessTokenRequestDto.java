package com.example.matchup.matchupbackend.global.config.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequestDto {

    private String refreshToken;
}
