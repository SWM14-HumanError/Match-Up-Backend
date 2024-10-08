package com.example.matchup.matchupbackend.dto.request.profile;

import com.example.matchup.matchupbackend.entity.MeetingType;
import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.error.annotation.Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class UserProfileEditRequest {

    private String imageName;

    private String imageBase64;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(max = 20, message = "닉네임은 20글자를 넘을 수 없습니다.")
    private String nickname;

    @Size(max = 50, message = "만남 장소는 50글자를 넘길 수 없습니다.")
    private String meetingAddress;

    @Size(max = 50, message = "만남 시간은 50글자를 넘길 수 없습니다.")
    private String meetingTime;

    @Enum(enumClass = MeetingType.class, message = "온라인, 오프라인, 상관없음으로만 입력할 수 있습니다.")
    private String meetingType;

    @Size(max = 200, message = "만남의 세부사항은 200글자를 넘길 수 없습니다.")
    private String meetingNote;

    @Size(max = 700, message = "소개는 700자를 넘길 수 없습니다.")
    private String introduce;

    private Map<@Enum(enumClass = RoleType.class, message = "적절하지 않은 RoleType 입니다.") String, Integer> userPositionLevels;

    private Map<@Size(max = 20, message = "sns 이름은 20글자를 넘길 수 없습니다.") String, String> Link;

    public MeetingType getMeetingType() {
        return (this.meetingType != null) ? MeetingType.valueOf(this.meetingType) : null;
    }
}
