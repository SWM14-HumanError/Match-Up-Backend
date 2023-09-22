package com.example.matchup.matchupbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Slf4j
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class AuditingConfig implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()) { // 인증이 안되어있는 경우
            return Optional.empty();
        }
        User principal = (User) authentication.getPrincipal();
        return Optional.of(Long.parseLong(principal.getUsername()));
    }
}
