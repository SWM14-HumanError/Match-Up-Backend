package com.example.matchup.matchupbackend.dto.request.user;

import com.example.matchup.matchupbackend.entity.MeetingType;
import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ProfileRequest {

    /*
    이메일과 아이디 값은 회원가입 시에, 이용약관 동의하고 정보를 기입한 사람만이 토큰을 받을 수 있도록 하기 위한 장치입니다.
    프로필 수정에도 이 DTO를 사용하기 때문에 request에서 검증하지 않고 서비스 계층에서 검증합니다.
     */
    @Email
    private String email;
    private Long id;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(max = 20, message = "닉네임은 20글자를 넘을 수 없습니다.")
    private String nickname;

    private String pictureName;

    private String pictureUrl;

    // todo: LocalDate validation
    private LocalDate birthDay;

    @Range(max = 100L, message = "개발자 연차는 100년을 넘거나 음수일 수 없습니다.")
    private Long expYear;

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

    private List<@Valid ProfileTagPositionRequest> profileTagPositions;

    private Map<@Size(max = 20, message = "sns 이름은 20글자를 넘길 수 없습니다.") String, @URL(message = "적절하지 않은 Link입니다.") String> link;

    public MeetingType getMeetingType() {
        return (this.meetingType != null) ? MeetingType.valueOf(this.meetingType) : null;
    }

    public List<RoleType> getTypeList() {
        return this.getProfileTagPositions().stream()
                .map(ProfileTagPositionRequest::getType)
                .toList();
    }

    public List<ProfileTagPositionRequest> getProfileTagPositions() {
        return this.profileTagPositions != null ? this.profileTagPositions : new ArrayList<>();
    }
}
