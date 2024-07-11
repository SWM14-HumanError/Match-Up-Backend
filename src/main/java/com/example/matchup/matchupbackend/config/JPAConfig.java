package com.example.matchup.matchupbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@RequiredArgsConstructor
@Configuration
@EnableJpaRepositories(basePackages =
        {"com.example.matchup.matchupbackend.repository",
                "com.example.matchup.matchupbackend.entity"})
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
public class JPAConfig {
}
