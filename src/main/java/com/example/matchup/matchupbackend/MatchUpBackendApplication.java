package com.example.matchup.matchupbackend;

import com.example.matchup.matchupbackend.entity.Role;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;


import java.util.Arrays;
@RequiredArgsConstructor
@SpringBootApplication
public class MatchUpBackendApplication {

    private final Environment environment;
    private final UserRepository userRepository;

    @Profile("local")
    @PostConstruct
    public void createDummyUser() {
        if(Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            User user = User.builder()
                    .nickname("test")
                    .email("test@test.com")
                    .isMentor(true)
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(user);

            for (int i = 0; i < 5; i++) {
                userRepository.save(User.builder()
                        .nickname("test" + i)
                        .email("test%d@test.com".formatted(i))
                        .isMentor(true)
                        .role(Role.USER)
                        .build());
            }
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(MatchUpBackendApplication.class, args);
    }

}
