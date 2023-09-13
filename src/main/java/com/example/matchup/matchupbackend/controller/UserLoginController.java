package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.config.jwt.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {

    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }

    /**
     * 액세스 토큰이 만료되었을 때, 리프레시 토큰을 이용해 액세스 토큰을 재발급
     * 이용약관에 동의하지 않은, 최초 로그인 유저는 이용약관 동의 페이지로 이동하기 위해서 signup query를 추가함
     * 관련 값은 tokenService.createNewAccessToken(token)에서 처리
      */
    @PostMapping("/login/token/refresh")
    public String loginToken(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                                .filter(cookie -> cookie.getName().equals("refresh_token"))
                                .map(Cookie::getValue)
                                .findFirst()
                                .orElse(null);
        if (refreshToken == null)  return "redirect:/login";

        String reissuedAccessToken = tokenService.createNewAccessToken(refreshToken);
        return "redirect:" + tokenProvider.getOAuth2LoginUrl().getSuccessUrl() + "?token=" + reissuedAccessToken;
    }
}
