package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {

    @GetMapping("/login")
    public String login() {

        return "oauth";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }

    private void getRedirectUri(HttpServletRequest request, HttpServletResponse response) {

        String requestURL = request.getHeader("Referer");
        String redirectURI = UriComponentsBuilder.fromUriString(requestURL).build().getPath();
        CookieUtil.addCookie(response, "redirect_uri", redirectURI, -1);
    }
}
