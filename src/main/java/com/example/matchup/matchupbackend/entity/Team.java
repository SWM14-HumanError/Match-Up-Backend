package com.example.matchup.matchupbackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team")
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private Long type; //스터디인지 프로젝트 모임인지
    @Column(name = "detail_type")
    private String detailType;

    @Column(name = "thumbnailUrl")
    private String thumbnailUrl;

    @Column(name = "content_like")
    private Long like;
    private String recruitFinish;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamTag> teamTagList = new ArrayList<>();

    @Builder //신규로 팀을 만들때 사용
    public Team(String title, String description, Long type, String detailType, String thumbnailUrl, Long like, String recruitFinish) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.detailType = detailType;
        this.thumbnailUrl = thumbnailUrl;
        this.like = like;
        this.recruitFinish = recruitFinish;
    }

    public void addTeamTagList(TeamTag teamTag)
    {
        teamTagList.add(teamTag);
    }

}