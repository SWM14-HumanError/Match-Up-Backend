package com.example.matchup.matchupbackend.global.config.oauth.handler;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.config.oauth.CustomOAuth2User;
import com.example.matchup.matchupbackend.global.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.matchup.matchupbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long id = (long) ((Math.random() * 899999999999L) + 100000000000L);
        User user = userService.saveRefreshToken(request, response, oAuth2User.getEmail(), id);

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken, user, id);

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getTargetUrl(String token, User user, Long id) {

        if (user.getAgreeTermOfService()) {
            return UriComponentsBuilder.fromUriString(
                            tokenProvider.getOAuth2LoginUrl().getSuccessUrl())
                    .queryParam("token", token)
                    .build()
                    .toUriString();
        } else {
            return UriComponentsBuilder.fromUriString(
                            tokenProvider.getOAuth2LoginUrl().getSuccessUrl())
                    .queryParam("email", user.getEmail())
                    .queryParam("id", id)
                    .build()
                    .toUriString();
        }

    }
}
