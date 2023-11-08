package com.example.matchup.matchupbackend.global.config.oauth.service;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.oauth.CustomOAuth2User;
import com.example.matchup.matchupbackend.global.config.oauth.dto.OAuthAttributes;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.service.FileService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;

@RequiredArgsConstructor
@Getter
@Slf4j
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());
        User user = saveOrUpdate(attributes);
        whenKakaoLoginProfileImageSave(registrationId, user);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.attributes(),
                attributes.nameAttributeKey(),
                user.getId(),
                user.getRole(),
                user.getEmail());
    }

    private void whenKakaoLoginProfileImageSave(String registrationId, User user) {
        if (registrationId.equals("kakao")) {
            try {
                String pictureUrl = user.getPictureUrl();
                URL imageUrl = new URL(pictureUrl);
                BufferedImage image = ImageIO.read(imageUrl);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", os);
                String imageBase64 = Base64.getEncoder().encodeToString(os.toByteArray());
                log.info("이거임 {}", imageBase64);

                if (imageBase64 != null) { //썸네일 사진이 있는 경우
                    UploadFile uploadFile = fileService.storeBase64ToFile(imageBase64, "%s_image.jpg".formatted(user.getNickname()));
                    user.setUploadFile(uploadFile);
                }
            } catch (IOException e) {
                log.info("{} 유저가 카카오로 로그인하면서 이미지를 불러오면서 오류 발생", user.getId());
            }
        }
    }

    private User saveOrUpdate(OAuthAttributes attributes) {

        if (attributes.email() == null) throw new UserNotFoundException("이메일값이 비어있습니다.");

        User user = userRepository.findByEmail(attributes.email())
                .map(entity -> entity.updateUserName(attributes.name()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
