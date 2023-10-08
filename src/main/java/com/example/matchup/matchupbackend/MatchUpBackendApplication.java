package com.example.matchup.matchupbackend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication()
public class MatchUpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchUpBackendApplication.class, args);
    }

}
