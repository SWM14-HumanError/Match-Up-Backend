package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.AdditionalUserInfoRequestDto;
import com.example.matchup.matchupbackend.dto.user.TechStack;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "picture_url")
    private String pictureUrl;
    @Column(name = "user_level")
    private Long userLevel;
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;
    /**
     * address(선호하는 장소)를 어떻게 받을지 고민
     */
    private String address;
    @Column(name = "user_email", unique = true)
    private String email;
    @Column(name = "user_birthday")
    private LocalDate birthday;
    @Column(name = "position")
    private String position;
    @Column(name = "position_level")
    private Long positionLevel;
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
    private String refreshToken;
    private Boolean isAuth;
//    @Column(columnDefinition = "BOOLEAN DEFAULT true") // 처음 로그인할 때, 이용약관 동의, 추가 정보 조회에 관여
    private Boolean isFirstLogin = true;
    //링크는 조인해서 가져온다
    private String expertize;
    private Long expYear;
    private String certificateURL;


//    public void addTeamUser(TeamUser teamUser) {
//        teamUserList.add(teamUser);
//        teamUser.setTag(this);
//    }
    //== 비즈니스 로직 ==//
    public void updateNewRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

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

    public User update(String nickname) {

        this.name = nickname;
        return this;
    }

    public String getRoleKey() {

        return this.role.getKey();
    }

    @Builder // OAuth2.0 로그인으로 얻은 최소한의 정보들로 User 객체 생성
    public User(String email, String name, String pictureUrl, Role role) {

        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.role = role;
    }

    public User updateFirstLogin(AdditionalUserInfoRequestDto dto) {

        this.userLevel = dto.getUserLevel();
        this.birthday = dto.getUserBirthday();
        this.address = dto.getAddress();
        this.expYear = dto.getExpYear();
        this.expertize = dto.getExpertize();
        this.position = dto.getPosition();
        this.positionLevel = dto.getPositionLevel();
        this.meetingType = dto.getMeetingType();
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(getRoleKey()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
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
