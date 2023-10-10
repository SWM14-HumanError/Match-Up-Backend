package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_tag")
public class UserTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long id;

    @Column(name = "tag_name")
    private String tagName;

    @Enumerated(EnumType.STRING)
    private RoleType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public UserTag(String tagName, RoleType type, User user, Tag tag) {
        this.tagName = tagName;
        this.type = type;
        this.user = user;
        this.tag = tag;
    }

    //== 비즈니스 로직 ==//
    public static UserTag create(String tagName, RoleType type, Tag tag, User user) {
        return UserTag.builder()
                .tag(tag)
                .user(user)
                .tagName(tagName)
                .type(type)
                .build();
    }
}
