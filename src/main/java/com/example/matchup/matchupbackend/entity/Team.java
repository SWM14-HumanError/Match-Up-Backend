package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.user.TechStack;
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
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    @Column(name = "content_like")
    private Long like;
    @Column(name = "On_Offline")
    private String onOffline;
    @Column(name = "city")
    private String city;
    @Column(name = "detail_spot")
    private String detailSpot;
    @Column(name = "recruit_finish")
    private String recruitFinish;
    @Column(name = "is_deleted")
    private Long isDeleted = 0L;
    @Column(name = "leader_id")
    private Long leaderID;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamTag> teamTagList = new ArrayList<>();
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamUser> teamUserList = new ArrayList<>();
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamMentoring> teamMentoringList = new ArrayList<>();
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamPosition> teamPositionList = new ArrayList<>();

    @Builder //신규로 팀을 만들때 사용
    public Team(String title, String description, Long type, String detailType, String thumbnailUrl, Long like, String onOffline, String city, String detailSpot, String recruitFinish, Long leaderID, List<TeamPosition> teamPositionList) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.detailType = detailType;
        this.thumbnailUrl = thumbnailUrl;
        this.like = like;
        this.onOffline = onOffline;
        this.city = city;
        this.detailSpot = detailSpot;
        this.recruitFinish = recruitFinish;
        this.leaderID = leaderID;
        this.teamPositionList = teamPositionList;
    }

    //== 연관관계 메서드 ==//
    public void addTeamTagList(TeamTag teamTag) {
        teamTagList.add(teamTag);
    }

    //== 비즈니스 로직 ==//
    public Long updateTeam(TeamCreateRequest teamCreateRequest) {
        this.thumbnailUrl = teamCreateRequest.getThumbnailUrl();
        this.title = teamCreateRequest.getName();
        this.type = teamCreateRequest.getType().getTeamType();
        this.detailType = teamCreateRequest.getType().getDetailType();
        this.onOffline = teamCreateRequest.getMeetingSpot().getOnOffline();
        this.city = teamCreateRequest.getMeetingSpot().getCity();
        this.detailSpot = teamCreateRequest.getMeetingSpot().getDetailSpot();
        return this.id;
    }

    public Long deleteTeam() {
        isDeleted = 1L;
        return this.id;
    }

    public Long numberOfUserByPosition(String position) {
        Long result = 0L;
        for (TeamUser teamUser : teamUserList) {
            if (teamUser.getApprove() == true && position.equals(teamUser.getRole())) {
                result++;
            }
        }
        return result;
    }

    public Long numberOfApprovedUser() {
        Long number = 0L;
        for (TeamUser teamUser : teamUserList) {
            if (teamUser.getApprove() == true) {
                number++;
            }
        }
        return number;
    }

    public List<TechStack> returnStackList() {
        List<TechStack> techStacks = new ArrayList<>();
        teamTagList.stream().forEach(teamTag -> {
            techStacks.add(TechStack.builder().tagID(teamTag.getId())
                    .tagName(teamTag.getTagName()).build());
        });
        return techStacks;
    }
}