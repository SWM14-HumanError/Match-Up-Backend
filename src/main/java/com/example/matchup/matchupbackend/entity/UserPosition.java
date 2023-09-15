package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.user.AdditionalUserInfoRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserPosition extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_position_id")
    private Long id;
    private String positionName;
    private Integer positionLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 최초 로그인 시에 유저의 기술 스택을 저장하기 위해 사용
     * @param request: AdditionalUserInfoRequest
     * @param stack: UserPositionType
     * @param user: User
     */
    @Builder
    public UserPosition(AdditionalUserInfoRequest request, UserPositionType stack, User user) {
        if (request.getUserPositionLevels().containsKey(stack)) {
            this.positionName = stack.toString();
            this.positionLevel = request.getUserPositionLevels().get(stack);
            this.user = user;
        }
    }


    public UserPosition updateUserProfile(Map<UserPositionType, Integer> userPositionLevels) {
        UserPositionType userPositionType = UserPositionType.valueOf(this.positionName);
        if (userPositionLevels.containsKey(userPositionType)) {
            this.positionLevel = userPositionLevels.get(userPositionType);
        }

        return this;
    }
}
