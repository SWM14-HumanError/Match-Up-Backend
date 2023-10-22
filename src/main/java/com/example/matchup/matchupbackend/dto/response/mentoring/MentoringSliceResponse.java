package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.Mentoring;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class MentoringSliceResponse {

    private List<MentoringSearchResponse> mentoringSearchResponses;

    private int size;

    private Boolean hasNextSlice;

    @Builder
    private MentoringSliceResponse(List<MentoringSearchResponse> mentoringSearchResponses, int size, Boolean hasNextSlice) {
        this.mentoringSearchResponses = mentoringSearchResponses;
        this.size = size;
        this.hasNextSlice = hasNextSlice;
    }

    public static MentoringSliceResponse of(Slice<Mentoring> pageOfMentoringSearchSlice, Map<Mentoring, Long> mentoringToLikesCountMap, Map<Mentoring, Boolean> mentoringToCheckLikeMap) {
        List<Mentoring> mentorings = pageOfMentoringSearchSlice.getContent();
        int size = pageOfMentoringSearchSlice.getSize();
        boolean hasNextSlice = pageOfMentoringSearchSlice.hasNext();

        return MentoringSliceResponse.builder()
                .mentoringSearchResponses(mentorings.stream()
                        .map(mentoring -> MentoringSearchResponse.ofSearch(
                                mentoring,
                                mentoringToLikesCountMap.get(mentoring),
                                0L,
                                mentoringToCheckLikeMap.get(mentoring)))
                        .toList())
                .size(size)
                .hasNextSlice(hasNextSlice)
                .build();
    }
}
