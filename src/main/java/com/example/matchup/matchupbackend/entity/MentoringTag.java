package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class MentoringTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentoring_tag_id")
    private Long id;

    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentoring_id")
    private Mentoring mentoring;

    @Builder
    private MentoringTag(String tagName, Mentoring mentoring) {
        this.tagName = tagName;
        this.mentoring = mentoring;
    }

    public static MentoringTag create(String tagName, Mentoring mentoring) {
        return MentoringTag.builder()
                .tagName(tagName)
                .mentoring(mentoring)
                .build();
    }
}
