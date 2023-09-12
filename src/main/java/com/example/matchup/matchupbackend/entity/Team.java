package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.TechStack;
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
public class Team extends BaseEntity {

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
    @Column(name = "thumbnail_upload_url")
    private String thumbnailUploadUrl;
    @Column(name = "thumbnail_url")
    private String thumbnailUrl; //storeURL
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
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Feedback> teamUserFeedbackList = new ArrayList<>();

    @Builder
    public Team(String title, String description, Long type, String detailType, String thumbnailUploadUrl, String thumbnailStoreUrl, Long like, String onOffline, String city, String detailSpot, String recruitFinish, Long leaderID, List<TeamPosition> teamPositionList) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.detailType = detailType;
        this.thumbnailUploadUrl = thumbnailUploadUrl;
        this.thumbnailUrl = thumbnailStoreUrl;
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
    public Long updateTeam(TeamCreateRequest teamCreateRequest, UploadFile uploadFile) {
        this.thumbnailUrl = String.valueOf(uploadFile.getS3Url());
        this.thumbnailUploadUrl = uploadFile.getUploadFileName();
        this.title = teamCreateRequest.getName();
        this.type = teamCreateRequest.getType().getTeamType();
        this.detailType = teamCreateRequest.getType().getDetailType();
        this.description = teamCreateRequest.getDescription();
        this.onOffline = teamCreateRequest.getMeetingSpot().getOnOffline();
        this.city = teamCreateRequest.getMeetingSpot().getCity();
        this.detailSpot = teamCreateRequest.getMeetingSpot().getDetailSpot();
        return this.id;
    }

    public void deleteTeam() {
        isDeleted = 1L;
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

    public static Team of(Long leaderID, TeamCreateRequest teamCreateRequest, UploadFile uploadFile) {
        Team build = Team.builder()
                .title(teamCreateRequest.getName())
                .description(teamCreateRequest.getDescription())
                .type(teamCreateRequest.getType().getTeamType())
                .detailType(teamCreateRequest.getType().getDetailType())
                .thumbnailUploadUrl(uploadFile.getUploadFileName())
                .thumbnailStoreUrl(String.valueOf(uploadFile.getS3Url())) //todo DB에 uploadName, storeName 둘다 저장하는것이 맞는지
                .like(0L)
                .onOffline(teamCreateRequest.getMeetingSpot().getOnOffline())
                .city(teamCreateRequest.getMeetingSpot().getCity())
                .detailSpot(teamCreateRequest.getMeetingSpot().getDetailSpot())
                .recruitFinish("NF")
                .leaderID(leaderID)
                .build();
        return build;
    }
}