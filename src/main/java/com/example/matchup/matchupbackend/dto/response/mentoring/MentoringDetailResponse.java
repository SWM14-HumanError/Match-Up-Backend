package com.example.matchup.matchupbackend.dto.response.mentoring;

import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.entity.MentoringTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MentoringDetailResponse {

    private String content;

    private List<String> stacks;

    @Builder
    private MentoringDetailResponse(String content, List<String> stacks) {
        this.content = content;
        this.stacks = stacks;
    }

    public static MentoringDetailResponse of(Mentoring mentoring) {
        return MentoringDetailResponse.builder()
                .content(mentoring.getContent())
                .stacks(mentoring.getMentoringTags().stream().map(MentoringTag::getTagName).toList())
                .build();
    }
}
