package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long id;

    private String companyName;
    
    private String title;
    
    private String description;

    private LocalDateTime deadLine;

    private String applyLink;

    @Enumerated(EnumType.STRING)
    private JobPosition jobPosition;

    @Enumerated(EnumType.STRING)
    private JobType jobType;
}
