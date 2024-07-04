package com.example.matchup.matchupbackend.dto.request.enterprise;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnterpriseStateRequest {
    @NotNull(message = "enterpriseApplyId는 필수 값 입니다.")
    private Long enterpriseApplyId;
    @NotNull(message = "isAccepted는 필수 값 입니다.")
    private Boolean isAccepted;
}
