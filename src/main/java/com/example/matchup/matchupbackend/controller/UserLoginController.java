package com.example.matchup.matchupbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {

    /**
     * 로그인 성공 시, SuccessHandler에 따라서 redirect 된다.
     * redirect에 포함된 query parameter에 access 토큰 값이 포함되며,
     * 서비스에 필요한 최소 정보와 이용약관에 동의하지 않은 유저는 signup=true를 반환
     */
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }
}
