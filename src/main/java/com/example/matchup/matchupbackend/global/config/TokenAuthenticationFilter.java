package com.example.matchup.matchupbackend.global.config;

import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.util.CookieUtil;
import com.example.matchup.matchupbackend.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final TokenProvider  tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(TokenProvider.HEADER_AUTHORIZATION);
            if (isSocketRequest(request)) {
                return;
            }
            if (tokenProvider.validTokenInFilter(authorizationHeader)) {
                userService.updateUserLastLogin(authorizationHeader);
                Authentication authentication = tokenProvider.getAuthentication(authorizationHeader);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            CookieUtil.deleteCookie(request, response, "token");
            CookieUtil.deleteCookie(request, response, "expires");
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isSocketRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith("/ws-stomp");
    }
}
