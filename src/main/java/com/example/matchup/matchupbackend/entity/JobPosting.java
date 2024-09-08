package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    private String imgName;

    private String imgUrl; // S3 URL

    private String applyLink;

    @Enumerated(EnumType.STRING)
    private JobPosition jobPosition;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Builder
    public JobPosting(String companyName, String title, String description, LocalDateTime deadLine, String imgName, String imgUrl, String applyLink, JobPosition jobPosition, JobType jobType) {
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.deadLine = deadLine;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.applyLink = applyLink;
        this.jobPosition = jobPosition;
        this.jobType = jobType;
    }

    //== 비즈니스 로직 ==//
    public static JobPosting from(UploadFile uploadFile, JobPostingRequest request) {
        return JobPosting.builder()
                .companyName(request.getCompanyName())
                .title(request.getTitle())
                .description(request.getDescription())
                .deadLine(request.getDeadLine())
                .imgName(uploadFile.getStoreFileName())
                .imgUrl(String.valueOf(uploadFile.getS3Url()))
                .applyLink(request.getApplyLink())
                .jobPosition(request.getJobPosition())
                .jobType(request.getJobType())
                .build();
    }

    public static JobPosting from(JobPostingRequest request) {
        return JobPosting.builder()
                .companyName(request.getCompanyName())
                .title(request.getTitle())
                .description(request.getDescription())
                .deadLine(request.getDeadLine())
                .imgName("null")
                .imgUrl("null")
                .applyLink(request.getApplyLink())
                .jobPosition(request.getJobPosition())
                .jobType(request.getJobType())
                .build();
    }
}
