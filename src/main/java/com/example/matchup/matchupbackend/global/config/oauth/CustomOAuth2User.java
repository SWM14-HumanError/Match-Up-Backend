package com.example.matchup.matchupbackend.global.config.oauth;

import com.example.matchup.matchupbackend.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Role role;
    private final String email;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            Role role, String email) {

        super(authorities, attributes, nameAttributeKey);
        this.role = role;
        this.email = email;
    }
}
