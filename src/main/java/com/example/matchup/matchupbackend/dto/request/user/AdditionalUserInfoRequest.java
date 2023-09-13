package com.example.matchup.matchupbackend.dto.request.user;

import com.example.matchup.matchupbackend.entity.UserPositionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.Map;

@Data
public class AdditionalUserInfoRequest {

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(max = 20, message = "닉네임은 20글자를 넘을 수 없습니다.")
    private String nickname;

    @Pattern(regexp="^(https:\\/\\/)?(?:www\\.)?[a-zA-Z0-9./]+$", message = "적절한 URL이 아닙니다.")
    private String pictureUrl;

    // todo: LocalDate validation
    private LocalDate birthDay;

    @Range(max = 100L, message = "개발자 연차는 100년을 넘거나 음수일 수 없습니다.")
    private Long expYear;
    private Map<UserPositionType, @Range(max = 5L, message = "스택의 범위는 0~5입니다.") Integer> userPositionLevels;
}
