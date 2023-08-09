package com.example.matchup.matchupbackend.global.config.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OAuthController {

    @GetMapping("/oauth2/authorize/{provider}")
    public String loginRequest(@PathVariable String provider, @RequestParam("redirect_uri") String redirectUri) {

        return "redirect:/oauth2/authorization/" + provider;
    }
}
