package com.example.matchup.matchupbackend.dto.request.mentoring;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewMentoringRequest {

    @NotNull
    @Min(10) @Max(50)
    private Integer satisfaction;

    @NotNull
    @Min(10) @Max(50)
    private Integer expertise;

    @NotNull
    @Min(10) @Max(50)
    private Integer punctuality;

    @NotBlank(message = "멘토링 리뷰글을 작성해야 합니다.")
    @Size(max = 20, message = "멘토링 리뷰는 20글자를 넘길 수 없습니다.")
    private String comment;

    public Double getSatisfaction() {
        return (double) satisfaction / 10;
    }

    public Double getExpertise() {
        return (double) expertise / 10L;
    }

    public Double getPunctuality() {
        return (double) punctuality / 10L;
    }
}
