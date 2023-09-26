package com.example.matchup.matchupbackend.dto.request.user;

import com.example.matchup.matchupbackend.entity.MeetingType;
import com.example.matchup.matchupbackend.entity.UserPositionType;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Map;

@Data
public class AdditionalUserInfoRequest {

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(max = 20, message = "닉네임은 20글자를 넘을 수 없습니다.")
    private String nickname;

    @URL(message = "적절하지 않은 이미지 URL입니다.")
    private String pictureUrl;

    // todo: LocalDate validation
    private LocalDate birthDay;

    @Range(max = 100L, message = "개발자 연차는 100년을 넘거나 음수일 수 없습니다.")
    private Long expYear;
    private Map<@Enum(enumClass = UserPositionType.class, message = "BACK, FRONT, FULL, DESIGN, AI로만 입력할 수 있습니다.") String, @Range(max = 5L, message = "스택의 범위는 0~5입니다.") Integer> userPositionLevels;
}
