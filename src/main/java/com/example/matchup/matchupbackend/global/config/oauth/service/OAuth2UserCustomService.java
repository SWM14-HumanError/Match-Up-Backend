package com.example.matchup.matchupbackend.global.config.oauth.service;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.global.config.oauth.CustomOAuth2User;
import com.example.matchup.matchupbackend.global.config.oauth.dto.OAuthAttributes;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.attributes(),
                attributes.nameAttributeKey(),
                user.getRole(),
                user.getEmail());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {

        if (attributes.email() == null) throw new IllegalArgumentException("Invalid email");

        // 검토 로그인 할 떄마다 getName으로 update하는 거 이상하지 않나?
        User user = userRepository.findByEmail(attributes.email())
                .map(entity -> entity.update(attributes.name()))
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }
}