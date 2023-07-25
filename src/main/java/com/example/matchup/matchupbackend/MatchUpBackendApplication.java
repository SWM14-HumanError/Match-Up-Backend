package com.example.matchup.matchupbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MatchUpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchUpBackendApplication.class, args);
    }

}
