package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

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

    // access token이 없는 채로 사이트에 접근하면 refresh token을 이용해 access token을 발급
//    @GetMapping("/token/validate")
//    public String validateToken(@RequestBody String refreshToken) {
//
//        tokenProvider.validToken();
//
//        return "redirect:/";
//    }

    // 액세스 토큰이 만료되었을 때, 리프레시 토큰을 이용해 액세스 토큰을 재발급
    @PostMapping("/login/token/refresh")
    public String loginToken(@RequestBody Map<String, String> headerAuthorization) {

        String token = tokenProvider.getAccessToken(headerAuthorization.get("refreshToken"));
        String reissuedAccessToken = tokenService.createNewAccessToken(token);
        return "redirect:" + tokenProvider.getOAuth2LoginUrl().getSuccessUrl() + "?token=" + reissuedAccessToken;
    }
}
