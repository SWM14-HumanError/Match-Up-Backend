package com.example.matchup.matchupbackend.global.config.oauth.dto;

import com.example.matchup.matchupbackend.entity.Role;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import com.example.matchup.matchupbackend.entity.User;

import java.util.Map;

@Slf4j
public record OAuthAttributes(
        Map<String, Object> attributes,
        String nameAttributeKey,
        String name,
        String email,
        String picture) {

    @Builder
    public OAuthAttributes {
    }

    public static OAuthAttributes of(
            String registrationId, Map<String, Object> attributes) {

        if ("naver".equals(registrationId)) {
            return ofNaver(attributes);
        } else if ("kakao".equals(registrationId)) {
            return ofKakao(attributes);
        }

        return ofGoogle(attributes);
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {

        String userNameAttributeName = "sub";

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {

        String userNameAttributeName = "id";
        log.info("attributes : " + attributes.toString());

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account"); // 검토 type cast check
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String email = ((boolean) kakaoAccount.get("is_email_valid")
                && (boolean) kakaoAccount.get("is_email_verified"))
                ? (String) kakaoAccount.get("email")
                : null;

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email(email)
                .picture((String) profile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {

        String userNameAttributeName = "id";
        Map<String, Object> response = (Map<String, Object>) attributes.get("response"); // 검토 type cast check

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .pictureUrl(picture)
                .role(Role.USER)
                .build();
    }
}