package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.servicecenter.OneOnOneInquiryRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ServiceCenter extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private ServiceCenter(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static ServiceCenter createOneOnOneInquiry(OneOnOneInquiryRequest request, User user) {
        return ServiceCenter.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user).build();
    }
}
