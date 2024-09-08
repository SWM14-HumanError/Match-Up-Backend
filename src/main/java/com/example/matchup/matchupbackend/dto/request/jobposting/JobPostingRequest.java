package com.example.matchup.matchupbackend.dto.request.jobposting;

import com.example.matchup.matchupbackend.entity.JobPosition;
import com.example.matchup.matchupbackend.entity.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingRequest {
    @NotBlank(message = "회사 명은 필수 입력 값")
    private String companyName;
    @NotBlank(message = "제목은 필수 입력 값")
    private String title;
    @NotBlank(message = "공고 설명은 필수 입력 값")
    private String description;
    @NotNull(message = "마감일은 필수 입력 값")
    private LocalDateTime deadLine;
    @NotBlank(message = "공고 링크는 필수 입력 값")
    private String applyLink;
    @NotNull(message = "직무는 필수 입력 값")
    private JobPosition jobPosition;
    @NotNull(message = "직종은 필수 입력 값")
    private JobType jobType;
    private String imageBase64;
    private String imageName;
}
