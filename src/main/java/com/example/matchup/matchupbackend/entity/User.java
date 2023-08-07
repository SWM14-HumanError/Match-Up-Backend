package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.user.TechStack;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "user")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "picture_url")
    private String pictureUrl;
    @Column(name = "user_level")
    private String userLevel;
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;
    /**
     * address(선호하는 장소)를 어떻게 받을지 고민
     */
    private String address;
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    @Column(name = "user_email")
    private String email;
    @Column(name = "user_birthday")
    private LocalDateTime birthday;
    @Column(name = "position")
    private String position;
    @Column(name = "position_level")
    private String positionLevel;
    @Column(name = "likes")
    private Long likes;
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    @Column(name = "review_score")
    private Double reviewScore = 0.0;
    @Column
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> userReviewList = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTag> userTagList = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TeamUser> teamUserList = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean isAuth;
    //링크는 조인해서 가져온다
    private String expertize;
    private String expYear;
    private String certificateURL;


//    public void addTeamUser(TeamUser teamUser) {
//        teamUserList.add(teamUser);
//        teamUser.setTag(this);
//    }
    //== 비즈니스 로직 ==//

    public double addUserReview(double score) {
        double totalScore = (this.reviewScore) * (this.totalReviews);
        this.totalReviews++;
        this.reviewScore = (totalScore + score) / this.totalReviews;
        return this.reviewScore;
    }

    public List<String> returnTagList() {
        return userTagList.stream().map(
                userTag -> userTag.getTag().getName()
        ).collect(Collectors.toList());
    }

    public List<TechStack> returnStackList(){
        List<TechStack> techStacks = new ArrayList<>();
        userTagList.stream().forEach(userTag -> {
            techStacks.add(TechStack.builder().tagID(userTag.getId())
                    .tagName(userTag.getTagName()).build());
        });
        return techStacks;
    }
}

/*
public Double returnUserReviewAverage() {
    Double totalScore = 0.0;
    for (Review userReview : userReviewList) {
        totalScore += userReview.getScore();
    }
    if (userReviewList.size() == 0) return 0.0;
    return totalScore / userReviewList.size();
}*/
