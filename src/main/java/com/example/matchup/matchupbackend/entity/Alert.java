package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "alert")
public class Alert extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name="is_read", columnDefinition = "boolean default false")
    private Boolean isRead;
    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted;
    @Column(name = "redirect_url")
    private String redirectUrl;
    @Enumerated(EnumType.STRING)
    private AlertType alertType;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
}
// 알림의 경우에 BaseTimeEntity에서 생성시간은 알림 생성 시간, 수정시간은 알림을 읽은 시간으로 설정하였습니다.