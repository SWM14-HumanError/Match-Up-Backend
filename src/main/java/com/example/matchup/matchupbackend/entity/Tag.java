package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tag_name")
    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<TeamTag> teamTagList = new ArrayList<>();
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "feed_id")
//    private Feed feed;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "mentoring_id")
//    private Mentoring mentoring;
    @Builder
    public Tag(String name) {
        this.name = name;
    }
}