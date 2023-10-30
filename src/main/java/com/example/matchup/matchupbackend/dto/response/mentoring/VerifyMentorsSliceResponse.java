package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.MentorVerify;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor
public class VerifyMentorsSliceResponse {

    private List<VerifyMentorsResponse> verifyMentorsResponses;

    private int size;

    private Boolean hasNextSlice;

    @Builder
    private VerifyMentorsSliceResponse(List<VerifyMentorsResponse> verifyMentorsResponses, int size, Boolean hasNextSlice) {
        this.verifyMentorsResponses = verifyMentorsResponses;
        this.size = size;
        this.hasNextSlice = hasNextSlice;
    }

    public static VerifyMentorsSliceResponse of(Slice<MentorVerify> mentorVerifySlice, Pageable pageable) {
        return VerifyMentorsSliceResponse.builder()
                .verifyMentorsResponses(mentorVerifySlice.getContent().stream()
                        .map(VerifyMentorsResponse::of).toList())
                .size(pageable.getPageSize())
                .hasNextSlice(mentorVerifySlice.hasNext())
                .build();
    }
}
