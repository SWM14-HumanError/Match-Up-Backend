package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserSnsLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sns_link_id")
    private Long id;

    private String linkType;

    private String linkUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @Builder
    public UserSnsLink(String linkType, String linkUrl, UserProfile userProfile) {
        this.linkType = linkType;
        this.linkUrl = linkUrl;
        this.userProfile = userProfile;
    }
}
