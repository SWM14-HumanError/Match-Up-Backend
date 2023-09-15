package com.example.matchup.matchupbackend.dto.request.profile;

import com.example.matchup.matchupbackend.entity.MeetingType;
import com.example.matchup.matchupbackend.entity.UserPositionType;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

@Data
public class UserProfileEditRequest {

    @URL(message = "적절하지 않은 이미지 URL입니다.")
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

    private Map<@Size(max = 20, message = "sns 이름은 20글자를 넘길 수 없습니다.") String,
            @URL(message = "적절하지 않은 Link입니다.") String> link;

    public MeetingType getMeetingType() {
        return (this.meetingType != null) ? MeetingType.valueOf(this.meetingType) : null;
    }
}
