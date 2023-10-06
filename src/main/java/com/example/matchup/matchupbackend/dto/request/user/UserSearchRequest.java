package com.example.matchup.matchupbackend.dto.request.user;

import lombok.Data;

@Data
public class UserSearchRequest {
    private String search; // 닉네임 검색
    private String place; //사는곳, 만남 위치
    private String position; // 하고 싶은 포지션
    private String orderBy = "default"; //정렬 기준
    private int page;
    private int size;
}
