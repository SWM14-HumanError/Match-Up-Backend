package com.example.matchup.matchupbackend.entity;

import com.example.matchup.matchupbackend.dto.request.profile.UserProfileEditRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserProfile extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Long id;

    @Column(length = 700)
    private String introduce;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;
    private String meetingAddress;
    private String meetingTime;
    private String meetingNote;

    @OneToOne(mappedBy = "userProfile")
    private User user;

    @OneToMany(mappedBy = "userProfile")
    private List<UserSnsLink> userSnsLinks = new ArrayList<>();

    /**
     * user profile 조회 시, userProfile NPE 방지
     * @param user: User
     */
    @Builder
    public UserProfile(User user) {
        this.user = user;
    }

    public UserProfile updateUserProfile(UserProfileEditRequest request) {
        this.introduce = request.getIntroduce();
        this.meetingAddress = request.getMeetingAddress();
        this.meetingTime = request.getMeetingTime();
        this.meetingType = request.getMeetingType();
        this.meetingNote = request.getMeetingNote();

        return this;
    }
}
