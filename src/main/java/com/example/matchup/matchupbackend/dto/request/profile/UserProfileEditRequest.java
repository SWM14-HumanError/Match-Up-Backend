package com.example.matchup.matchupbackend.dto.request.profile;

import com.example.matchup.matchupbackend.entity.MeetingType;
import com.example.matchup.matchupbackend.entity.UserPositionType;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class UserProfileEditRequest {

    @Pattern(regexp="^(https:\\/\\/)?(?:www\\.)?[a-zA-Z0-9./]+$", message = "적절한 URL이 아닙니다.")
    private String pictureUrl;

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

    @Size(max = 5, message = "BACK, FRONT, FULL, AI, DESIGN의 level을 받습니다.")
    private Map<UserPositionType, Integer> userPositionLevels;

    private Map<@Size(max = 50, message = "sns 이름은 20글자를 넘길 수 없습니다.") String,
            @Pattern(regexp="^(https:\\/\\/)?(?:www\\.)?[a-zA-Z0-9./]+$", message = "적절한 URL이 아닙니다.") String> link;

    public MeetingType getMeetingType() {
        return MeetingType.valueOf(this.meetingType);
    }
}
