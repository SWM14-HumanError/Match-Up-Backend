package com.example.matchup.matchupbackend.dto.response.profile;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserProfileFeedbackResponse {
    private Map<String, List<String>> detailFeedbacks; // {"GREAT" : ["코딩의 신이에요", "착해요"], "NORMAL" : ["잘해요"], BAD : ["잠수탔어요"]}
}
