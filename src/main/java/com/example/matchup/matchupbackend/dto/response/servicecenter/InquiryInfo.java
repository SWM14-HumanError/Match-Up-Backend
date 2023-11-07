package com.example.matchup.matchupbackend.dto.response.servicecenter;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InquiryInfo {
    private String title;
    private String content;
    private String createdAt;
    private String userNickname;
    private String userEmail;

    @Builder
    public InquiryInfo(String title, String content, String createdAt, String userNickname, String userEmail) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
    }
}
