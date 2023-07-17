package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "alert")
public class Alert {

    @Id
    @Column(name = "alert_id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "alert_type")
    private Integer alertType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}