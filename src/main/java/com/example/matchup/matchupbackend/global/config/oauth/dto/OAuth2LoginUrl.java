package com.example.matchup.matchupbackend.global.config.oauth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2")
@Getter
@Setter
public class OAuth2LoginUrl {

    private String logoutSuccessUrl;
    private String successUrl;
}