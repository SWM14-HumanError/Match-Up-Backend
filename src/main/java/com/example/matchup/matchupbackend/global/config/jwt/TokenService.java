package com.example.matchup.matchupbackend.global.config.jwt;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.ExpiredTokenException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.TokenRefreshNotPermitException;
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
    private final UserRepository userRepository;

    public String createNewAccessToken(String refreshToken) {
        log.info("token: {}", refreshToken);
        if (refreshToken == null || tokenProvider.validToken(refreshToken) == TokenStatus.EXPIRED) {
            throw new ExpiredTokenException("만료된 refresh 토큰으로 접근하였거나 토큰이 없습니다.");
        } else if (tokenProvider.validToken(refreshToken) == TokenStatus.EXPIRED) {
            throw new AuthorizeException("손상된 refresh 토큰으로 접근하였습니다.");
        }

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(TokenRefreshNotPermitException::new);
        String response = tokenProvider.generateToken(user, Duration.ofHours(2));
        return (user.getIsFirstLogin()) ? response + "signup=true" : response;
    }
}
