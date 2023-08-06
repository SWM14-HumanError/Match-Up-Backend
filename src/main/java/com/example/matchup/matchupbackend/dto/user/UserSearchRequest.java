package com.example.matchup.matchupbackend.dto.user;

import lombok.Data;

@Data
public class UserSearchRequest {
    private String search; // 이름 검색
    private String place; //사는곳, 만남 위치
    private String techStack; //가지고 있는 기술 스택
    private String position; // 하고 싶은 포지션
    private Long orderBy = 0L; //정렬 기준
    private int page;
    private int size;
}
