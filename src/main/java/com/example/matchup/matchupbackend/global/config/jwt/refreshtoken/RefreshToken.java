package com.example.matchup.matchupbackend.global.config.jwt.refreshtoken;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refreshtoken_id", updatable = false)
    private Long id;

    // 검토, 왜 one to one mapping 안 했지?
    @Column(nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    // 검토, DDD 관점에서 domain에 있는 비즈니스 로직은 이벤트에 맞춰서 메소드 명을 정하는 것으로 알고 있다.
    public RefreshToken update(String newRefreshToken) {

            this.refreshToken = newRefreshToken;
            return this;
    }
}
