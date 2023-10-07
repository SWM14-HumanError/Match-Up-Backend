package com.example.matchup.matchupbackend.dto.response.profile;

import com.example.matchup.matchupbackend.dto.response.team.TeamSearchResponse;
import com.example.matchup.matchupbackend.dto.response.userposition.UserPositionDetailResponse;
import com.example.matchup.matchupbackend.entity.MeetingType;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserProfileDetailResponse {

    private String pictureUrl;
    private String nickname;
    private Long bestPositionLevel;
    private Double feedbackScore;
    private Map<String, String> snsLinks;
    private Boolean isMentor;
    private Boolean isAuth;
    private ZonedDateTime lastLogin;
    private String introduce;
    private List<UserPositionDetailResponse> userPositions;
    private String meetingAddress;
    private String meetingTime;
    private MeetingType meetingType;
    private String meetingNote;
    private List<TeamSearchResponse> projects;
    private List<TeamSearchResponse> studies;
}
