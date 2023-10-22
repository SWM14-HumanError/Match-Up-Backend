package com.example.matchup.matchupbackend;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@RequiredArgsConstructor
@SpringBootApplication()
public class MatchUpBackendApplication {

    private final UserRepository userRepository;

    @Profile("local")
    @PostConstruct
    public void createDummyUser() {
        User user = User.builder()
                .nickname("test")
                .email("test@test.com")
                .isMentor(false)
                .build();
        userRepository.save(user);
    }
    public static void main(String[] args) {
        SpringApplication.run(MatchUpBackendApplication.class, args);
    }

}
