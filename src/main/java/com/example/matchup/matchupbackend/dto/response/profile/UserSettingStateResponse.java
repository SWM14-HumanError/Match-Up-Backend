package com.example.matchup.matchupbackend.dto.response.profile;

import com.example.matchup.matchupbackend.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
public class UserSettingStateResponse {
    private Boolean isProfileHider;
    private Boolean isFeedbackHider;

    @Builder
    private UserSettingStateResponse(Boolean isProfileHider, Boolean isFeedbackHider) {
        this.isProfileHider = isProfileHider;
        this.isFeedbackHider = isFeedbackHider;
    }

    public static UserSettingStateResponse fromEntity(User user) {
        return UserSettingStateResponse.builder()
                .isProfileHider(user.getProfileHider())
                .isFeedbackHider(user.getFeedbackHider())
                .build();
    }
}
