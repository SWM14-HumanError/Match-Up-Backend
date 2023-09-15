package com.example.matchup.matchupbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        log.info("AuditorAware: {}", authentication.getPrincipal());
//        String name = authentication.getPrincipal().getClass().getName();
//        log.info("AuditorAware: {}", name);
        return Optional.of(1L); //todo value에 세션에서 유저 아이디 가져와서 넣어야 합니다
    }
}
