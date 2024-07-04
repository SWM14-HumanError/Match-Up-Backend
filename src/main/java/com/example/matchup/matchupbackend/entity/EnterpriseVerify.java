package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.enterprise.EnterpriseVerifyFormRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnterpriseVerify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enterprise_verify_id")
    private Long id;

    private String content;

    private String enterpriseEmail;

    private String userEmail;

    private boolean isVerified;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Builder
    public EnterpriseVerify(String content, String enterpriseEmail, String userEmail, boolean isVerified, User user) {
        this.content = content;
        this.enterpriseEmail = enterpriseEmail;
        this.userEmail = userEmail;
        this.isVerified = isVerified;
        this.user = user;
    }

    //== 비즈니스 로직 ==//
    public static EnterpriseVerify from(EnterpriseVerifyFormRequest verifyFormRequest, User user) {
        return EnterpriseVerify.builder()
                .content(verifyFormRequest.getContent())
                .enterpriseEmail(verifyFormRequest.getEnterpriseEmail())
                .userEmail(user.getEmail())
                .isVerified(false)
                .user(user)
                .build();
    }
}
