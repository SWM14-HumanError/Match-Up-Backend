package com.example.matchup.matchupbackend.dto.response.enterprise;

import com.example.matchup.matchupbackend.entity.EnterpriseVerify;
import lombok.Builder;
import lombok.Data;

@Data
public class EnterpriseApply {
    private String content;
    private String enterpriseEmail;
    private String userNickname;
    private Boolean isAccepted;

    @Builder
    private EnterpriseApply(String content, String enterpriseEmail, String userNickname, Boolean isAccepted) {
        this.content = content;
        this.enterpriseEmail = enterpriseEmail;
        this.userNickname = userNickname;
        this.isAccepted = isAccepted;
    }

    public static EnterpriseApply from(EnterpriseVerify enterpriseVerify) {
        return EnterpriseApply.builder()
                .content(enterpriseVerify.getContent())
                .enterpriseEmail(enterpriseVerify.getEnterpriseEmail())
                .userNickname(enterpriseVerify.getUser().getNickname())
                .isAccepted(enterpriseVerify.isVerified())
                .build();
    }
}
