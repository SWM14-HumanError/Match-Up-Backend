package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.TechStack;
import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.user.ProfileRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String tokenId;

    @Column(name = "user_name") // 실제 이름 (가지고만 있어야 함)
    private String name;

    @Column(unique = true)
    private String nickname; // 닉네임
    @Column(name = "user_level")
    private Long userLevel;

    @Column(name = "user_birthday")
    private LocalDate birthDay;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    private String thumbnailUploadUrl;

    @Column(columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime lastLogin = LocalDateTime.now();

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isMentor = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAuth = false;

//    @Column(columnDefinition = "BOOLEAN DEFAULT false")
//    private Boolean agreeTermOfService = false;

    private Long agreeTermOfServiceId;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isUnknown = true;

    @Column(name ="feedbackHider", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean feedbackHider = false;

    @Column(name ="profileHider", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean profileHider = false;

    //링크는 조인해서 가져온다
    private String expertize;
    private Long expYear;
    private String certificateURL;

    /**
     * address(선호하는 장소)를 어떻게 받을지 고민
     */
    private String address;

    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "likes", columnDefinition = "bigint default 0")
    private Long likes = 0L;
    @Column(name = "total_feedbacks", columnDefinition = "integer default 0")
    private Integer totalFeedbacks = 0; // 팀원 상호 평가 갯수
    @Column(name = "feedback_score", columnDefinition = "double default 36.5")
    private Double feedbackScore = 36.5; // 팀원 상호 평가 온도
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTag> userTagList = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TeamUser> teamUserList = new ArrayList<>();
    @OneToMany(mappedBy = "giver", cascade = CascadeType.PERSIST)
    private List<Feedback> giveFeedbackList = new ArrayList<>();
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Feedback> recieveFeedbackList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Alert> alertList = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserPosition> userPositions = new ArrayList<>();
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserProfile userProfile;
    @OneToMany(mappedBy = "refusedUser", cascade = CascadeType.REMOVE)
    private List<TeamRefuse> teamRefuses = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<ServiceCenter> serviceCenters = new ArrayList<>();

    /**
     * Deprecated
     */
    private String position;
    private Long positionLevel;

    /**
     * OAuth2.0 로그인으로 얻은 최소한의 정보들로 User 객체 생성
     */
    @Builder
    public User(String email, String name, String pictureUrl, Role role) {
        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.role = role;
    }

    public void changeFeedbackHide(){
        this.feedbackHider = !this.feedbackHider;
    }

    public void changeProfileHide(){
        this.profileHider = !this.profileHider;
    }
//    public void addTeamUser(TeamUser teamUser) {
//        teamUserList.add(teamUser);
//        teamUser.setTag(this);
//    }

    //== 비즈니스 로직 ==//
    public void updateNewRefreshToken(String newRefreshToken, Long id) {
        this.refreshToken = newRefreshToken;
        this.agreeTermOfServiceId = id;
        if (this.tokenId == null) {
            String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

            SecureRandom random = new SecureRandom();
            String randomString = IntStream.range(0, 5)
                    .mapToObj(i -> CHARACTERS.charAt(random.nextInt(CHARACTERS.length())))
                    .map(String::valueOf)
                    .collect(Collectors.joining());
            String randomNumber = IntStream.range(0, 13)
                    .mapToObj(i -> Integer.toString(random.nextInt(10))) // 0부터 9까지의 숫자
                    .collect(Collectors.joining());
            this.tokenId = randomString + randomNumber;
        }
    }

    public List<String> returnTagList() {
        return userTagList.stream().map(
                userTag -> userTag.getTag().getName()
        ).collect(Collectors.toList());
    }

    public List<TechStack> returnStackList() {
        List<TechStack> techStacks = new ArrayList<>();
        userTagList.stream().forEach(userTag -> {
            techStacks.add(TechStack.builder().tagID(userTag.getId())
                    .tagName(userTag.getTagName()).build());
        });
        return techStacks;
    }

    public User updateUserName(String name) {
        this.name = name;
        return this;
    }

    public String getRoleKey() {

        return this.role.getKey();
    }

    public void addFeedback(Feedback feedback) {
        this.totalFeedbacks++; // 피드백 갯수 1 증가
        this.feedbackScore += feedback.getTotalScore(); // 피드백에 따른 개인 점수 증가
        this.recieveFeedbackList.add(feedback); // 피드백 리스트 추가
    }

    public User updateFirstLogin(ProfileRequest request, List<UserPosition> userPositions) {
        this.nickname = request.getNickname();
        this.birthDay = request.getBirthDay();
        this.expYear = request.getExpYear();
        this.userPositions = userPositions;
        this.userLevel = userPositions.stream().mapToLong(UserPosition::getTypeLevel).max().orElse(0L);
        this.isUnknown = false;

        return this;
    }

//    public User updateTermService() {
//        this.agreeTermOfService = true;
//        return this;
//    }

    public User updateUserLastLogin() {
        this.lastLogin = LocalDateTime.now();
        return this;
    }

    public User updateUserProfile(ProfileRequest request) {
        this.nickname = request.getNickname();
        return this;
    }

    public void addLike(){
        this.likes++;
    }

    public void deleteLike(){
        this.likes--;
    }

    public void setUploadFile(UploadFile uploadFile) {
        this.thumbnailUploadUrl = uploadFile.getUploadFileName();
        this.pictureUrl = String.valueOf(uploadFile.getS3Url());
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
