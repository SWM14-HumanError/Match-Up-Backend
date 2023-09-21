package com.example.matchup.matchupbackend.dto.response.profile;

import com.example.matchup.matchupbackend.entity.Feedback;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileFeedbackResponse {
    private List<String> detailFeedbacks; // {"GREAT" : ["코딩의 신이에요", "착해요"], "NORMAL" : ["잘해요"], BAD : ["잠수탔어요"]}

    public static UserProfileFeedbackResponse from(List<Feedback> feedbacks) {
        return UserProfileFeedbackResponse.builder()
                .detailFeedbacks(feedbacks.stream().map(feedback -> feedback.getCommentToUser()).toList())
                .build();
    }

    @Builder
    public UserProfileFeedbackResponse(List<String> detailFeedbacks) {
        this.detailFeedbacks = detailFeedbacks;
    }
}
