package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.user.ProfileTagPositionRequest;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserPosition extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_position_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType type;

    private Integer typeLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 최초 로그인 시에 유저의 기술 스택을 저장하기 위해 사용
     */
    @Builder
    public UserPosition(RoleType type, Integer typeLevel, User user) {
        this.type = type;
        this.typeLevel = typeLevel;
        this.user = user;
    }

    public static UserPosition create(ProfileTagPositionRequest userPosition, User user) {
        return UserPosition.builder()
                .type(userPosition.getType())
                .typeLevel(userPosition.getTypeLevel())
                .user(user)
                .build();
    }

    public UserPosition updateUserPosition(Integer typeLevel) {
        this.typeLevel = typeLevel;
        return this;
    }

    public static UserPosition createWhenProfileUpdate(@Valid ProfileTagPositionRequest profileTagPositionRequest, User user) {
        return UserPosition.builder()
                .type(profileTagPositionRequest.getType())
                .typeLevel(profileTagPositionRequest.getTypeLevel())
                .user(user)
                .build();
    }
}
