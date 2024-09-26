package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.UserProfileRepository;
import com.example.matchup.matchupbackend.service.UserProfileService;
import com.example.matchup.matchupbackend.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WebMvcTest
class UserProfileControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private UserProfileService userProfileService;

    @PostConstruct
    public void createUser() {
        User user = User.createUserForTest();
        userRepository.save(user);
    }
}