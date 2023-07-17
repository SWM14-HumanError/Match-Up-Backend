package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;

@Entity
public class Ability {
    @Id
    @Column(name = "ability_id")
    private Long id;
    private String position;
    private Integer level;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
