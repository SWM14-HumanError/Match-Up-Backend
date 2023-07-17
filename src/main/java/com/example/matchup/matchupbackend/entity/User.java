package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class User extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_name")
    private String name;
    private String pictureUrl;
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;
    /**
     * address(선호하는 장소)를 어떻게 받을지 고민
     */
    private String address;
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    @Column(name = "user_email")
    private String email;
    @Column(name = "user_birthday")
    private LocalDateTime birthday;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean isAuth;
    //링크는 조인해서 가져온다
    private String expertize;
    private String expYear;
    private String certificateURL;
}
