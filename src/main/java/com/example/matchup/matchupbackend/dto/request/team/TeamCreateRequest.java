package com.example.matchup.matchupbackend.dto.request.team;

import com.example.matchup.matchupbackend.dto.Member;
import com.example.matchup.matchupbackend.dto.response.team.MeetingSpotResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamTypeResponse;
import com.example.matchup.matchupbackend.global.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TeamCreateRequest {

    private String imageBase64;

    private String imageName;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 30, message = "제목은 30자를 넘을 수 없습니다.")
    private String name;

    @Valid
    @NotNull(message = "팀 타입은 필수 입력 값입니다.")
    private TeamTypeResponse type;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    @Size(max = 5000, message = "내용은 최대 5000자를 넘을 수 없습니다.")
    private String description;

    @Valid
    @NotNull(message = "모임 장소는 필수 입력 값입니다.")
    private MeetingSpotResponse meetingSpot;

    private String meetingDate;

    @Valid
    @NotEmpty(message = "모집 팀원은 필수 입력 값입니다.")
    private List<Member> memberList;

    public List<String> returnTagListByRole(RoleType role) //사용자의 태그들을 모아서 중복을 제거함
    {
        List<String> userStacks = new ArrayList<>();
        for (Member member : memberList) {
            if (role == member.getRole()) {
                userStacks.addAll(member.getStacks());
            }
        }
        return userStacks.stream().distinct().collect(Collectors.toList());
    }
    @Builder
    public TeamCreateRequest(String imageBase64, String imageName, String name, TeamTypeResponse type, String description, MeetingSpotResponse meetingSpot, String meetingDate, List<Member> memberList) {
        this.imageBase64 = imageBase64;
        this.imageName = imageName;
        this.name = name;
        this.type = type;
        this.description = description;
        this.meetingSpot = meetingSpot;
        this.meetingDate = meetingDate;
        this.memberList = memberList;
    }
}
