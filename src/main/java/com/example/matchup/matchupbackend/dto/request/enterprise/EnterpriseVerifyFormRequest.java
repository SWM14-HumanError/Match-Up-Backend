package com.example.matchup.matchupbackend.dto.request.enterprise;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnterpriseVerifyFormRequest {
    @NotBlank(message = "기업 설명을 작성해 주세요.")
    private String content;
    @Email(message = "이메일 형식이 아닙니다.")
    private String enterpriseEmail;

}
