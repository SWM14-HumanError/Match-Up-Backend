package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.TechStack;
import com.example.matchup.matchupbackend.dto.UploadFile;
import com.example.matchup.matchupbackend.dto.request.user.ProfileRequest;
import com.example.matchup.matchupbackend.dto.request.user.ProfileTagPositionRequest;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
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

import static com.example.matchup.matchupbackend.entity.Role.*;

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

    @Column(name = "user_name") // 실제 이름
    private String name;

    @Column(unique = true)
    private String nickname; // 닉네임

    @Column(name = "user_level")
    private Long userLevel;

    @Column(name = "user_birthday")
    private LocalDate birthDay;

    @Column(name = "picture_url")
    private String pictureUrl;

    private String position;

    private Long positionLevel;

    @Column(length = 500)
    private String refreshToken;

    private String thumbnailUploadUrl;

    private Long agreeTermOfServiceId;

    @Column(columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime lastLogin = LocalDateTime.now();

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isMentor = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAuth = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isUnknown = true;

    @Column(name ="isDeleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted = false;

    @Column(name ="feedbackHider", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean feedbackHider = false;

    @Column(name ="profileHider", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean profileHider = false;

    private Long expYear;

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

    @OneToMany(mappedBy = "refusedUser", cascade = CascadeType.REMOVE)
    private List<TeamRefuse> teamRefuses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ServiceCenter> serviceCenters = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<InviteTeam> inviteTeamReceivers = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<InviteTeam> inviteTeamSenders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserChatRoom> userChatRoom = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.REMOVE)
    private List<Mentoring> mentorings = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private MentorVerify mentorVerify;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * OAuth2.0 로그인으로 얻은 최소한의 정보들로 User 객체 생성
     */
    @Builder
    public User(String nickname, String email, String name, String pictureUrl, Role role, Boolean isMentor, Long agreeTermOfServiceId) {
        this.nickname = nickname;
        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.role = role;
        this.agreeTermOfServiceId = agreeTermOfServiceId;
        this.isMentor = isMentor;
    }

    //== 비즈니스 로직 ==//
    public static User createUserForTest() {
        return User.builder()
                .email("test@test.com")
                .nickname("test")
                .agreeTermOfServiceId(1483L)
                .role(USER)
                .build();
    }

    public void changeFeedbackHide(){
        this.feedbackHider = !this.feedbackHider;
    }

    public void changeProfileHide(){
        this.profileHider = !this.profileHider;
    }

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

    public void isAdmin() {
        if (this.role != ADMIN) {
            throw new AuthorizeException("관리자가 아닙니다.");
        }
    }

    public User updateUserLastLogin() {
        this.lastLogin = LocalDateTime.now();
        return this;
    }

    public User updateUserProfile(ProfileRequest request) {
        this.nickname = request.getNickname();
        if (request.getProfileTagPositions() != null) {
            this.userLevel = request.getProfileTagPositions().stream()
                    .mapToLong(ProfileTagPositionRequest::getTypeLevel).max().orElse(0L);
        }
        return this;
    }

    public void acceptMentor() {
        this.isMentor = true;
        this.role = MENTOR;
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

    public void deleteImage(){
        this.thumbnailUploadUrl = null;
        this.pictureUrl = null;
    }

    public void deleteUser(){
        this.isDeleted = true;
        this.email = null;
        this.nickname = null;
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
    for (ReviewMentor userReview : userReviewList) {
        totalScore += userReview.getScore();
    }
    if (userReviewList.size() == 0) return 0.0;
    return totalScore / userReviewList.size();
}*/
