package com.example.matchup.matchupbackend.dto.request.servicecenter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OneOnOneInquiryRequest {

    @NotBlank(message = "일대일 문의의 제목은 필수 입력 값입니다.")
    @Size(max = 50, message = "일대일 문의의 제목은 50글자를 넘길 수 없습니다.")
    private String title;

    @NotBlank(message = "일대일 문의의 내용은 필수 입력 값입니다.")
    @Size(max = 700, message = "일대일 문의의 내용은 700글자를 넘길 수 없습니다.")
    private String content;
}
