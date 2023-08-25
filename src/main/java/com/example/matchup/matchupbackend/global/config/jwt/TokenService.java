package com.example.matchup.matchupbackend.global.config.jwt;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {

        if (!tokenProvider.validToken(refreshToken)) {
            log.debug("Invalid refresh-token");
            throw new IllegalArgumentException("Invalid refresh-token");
        }

//        Long userId = userService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(5L);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
