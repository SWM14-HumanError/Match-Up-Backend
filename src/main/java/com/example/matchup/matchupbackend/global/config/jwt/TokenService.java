package com.example.matchup.matchupbackend.global.config.jwt;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRespository;

    public String createNewAccessToken(String refreshToken) {

        if (!tokenProvider.validToken(refreshToken)) throw new IllegalArgumentException("Invalid refresh-token");

        User user = userRespository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user found by refresh-token"));

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
