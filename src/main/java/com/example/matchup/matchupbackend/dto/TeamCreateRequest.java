package com.example.matchup.matchupbackend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TeamCreateRequest {
    private String thumbnailUrl;
    private String name;
    private TeamType type;
    private String description;
    private MeetingSpot meetingSpot;
    private List<Member> memberList;
    private Long leaderID;

    //계층구조가 있는 데이터는 굳이 클래스로 다 안나누고 1. 띄어쓰기 2. List로 보내고 받을수 있다(프론트와 상의)
    public List<String> returnTagList() //사용자의 태그들을 모아서 중복을 제거함
    {
        List<String> userStacks = new ArrayList<>();
        for (Member member : memberList) {
            userStacks.addAll(member.getStacks());
        }
        return userStacks.stream().distinct().collect(Collectors.toList());
    }

}
