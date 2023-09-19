package com.example.matchup.matchupbackend.dto.response.profile;

import com.example.matchup.matchupbackend.dto.request.teamuser.FeedbackGrade;
import com.example.matchup.matchupbackend.entity.Feedback;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UserProfileFeedbackResponse {
    private Map<String, List<String>> detailFeedbacks; // {"GREAT" : ["코딩의 신이에요", "착해요"], "NORMAL" : ["잘해요"], BAD : ["잠수탔어요"]}

    public static UserProfileFeedbackResponse of(String grade, List<String> feedbacks) {
        UserProfileFeedbackResponse userProfileFeedbackResponse = new UserProfileFeedbackResponse();
        Map<String, List<String>> detailFeedbacks = new HashMap<>();
        detailFeedbacks.put(grade, feedbacks);
        userProfileFeedbackResponse.setDetailFeedbacks(detailFeedbacks);
        return userProfileFeedbackResponse;
    }
}
