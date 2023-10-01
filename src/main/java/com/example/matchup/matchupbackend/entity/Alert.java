package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alert")
public class Alert extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "is_read", columnDefinition = "boolean default false")
    private boolean isRead = false;
    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted = false;
    @Column(name = "redirect_url")
    private String redirectUrl;
    @Enumerated(EnumType.STRING)
    private AlertType alertType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //==연관관계 매서드==//
    public void setUser(User user) {
        this.user = user;
    }

    @Builder
    public Alert(String title, String content, String redirectUrl, AlertType alertType, User user) {
        this.title = title;
        this.content = content;
        this.redirectUrl = redirectUrl;
        this.alertType = alertType;
        this.user = user;
    }

    public static Alert from(Alert alert) {
        return Alert.builder()
                .title(alert.getTitle())
                .content(alert.getContent())
                .redirectUrl(alert.getRedirectUrl())
                .alertType(alert.getAlertType())
                .build();
    }

    //==비즈니스 로직==//
    public void readAlert() {
        isRead = true;
    }

    public void deleteAlert() {
        isDeleted = true;
    }
}
// 알림의 경우에 BaseTimeEntity에서 생성시간은 알림 생성 시간, 수정시간은 알림을 읽은 시간으로 설정하였습니다.