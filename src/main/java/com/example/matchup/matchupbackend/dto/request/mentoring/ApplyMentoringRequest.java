package com.example.matchup.matchupbackend.dto.request.mentoring;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplyMentoringRequest {

    @NotNull(message = "멘토링 받을 팀을 선택해야합니다.")
    private Long teamId;

    @NotBlank(message = "핸드폰 번호는 필수 값입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
    private String phoneNumber;

    @NotBlank(message = "이메일은 필수 값입니다.")
    private String email;

    @NotBlank(message = "멘토링 신청 메세지는 필수 값입니다.")
    @Size(max = 200, message = "멘토링 신청의 메세지는 200글자를 넘길 수 없습니다.")
    private String content;
}
