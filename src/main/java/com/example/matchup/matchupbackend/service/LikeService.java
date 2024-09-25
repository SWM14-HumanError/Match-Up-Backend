package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.UserCardResponse;
import com.example.matchup.matchupbackend.dto.response.team.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamSearchResponse;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.dto.response.user.UserLikeResponse;
import com.example.matchup.matchupbackend.entity.Likes;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidLikeException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.LikeNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.repository.LikeRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.UserPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final AlertCreateService alertCreateService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserPositionRepository userPositionRepository;

    /**
     * 유저에게 좋아요를 주는 로직
     */
    @Transactional
    public void saveLikeToUser(Long likeGiverId, Long likeReceiverId) {
        validLikeToUser(likeGiverId, likeReceiverId);
        User likeGiver = userRepository.findById(likeGiverId).orElseThrow(() -> {
            throw new UserNotFoundException("좋아요를 준 유저를 찾을 수 없습니다.");
        });
        User likeReceiver = userRepository.findById(likeReceiverId).orElseThrow(() -> {
            throw new UserNotFoundException("좋아요를 받을 유저를 찾을 수 없습니다.");
        });
        likeReceiver.addLike();
        Likes likesToUser = Likes.builder()
                .user(likeGiver)
                .likeReceiver(likeReceiver)
                .build();
        likeRepository.save(likesToUser);
        alertCreateService.saveUserLikeAlert(likeGiver, likeReceiver, likeReceiver.getLikes());
    }

    private void validLikeToUser(Long likeGiverId, Long likeReceiverId) {
        if (likeGiverId.equals(likeReceiverId))
            throw new InvalidLikeException("나 자신에게 좋아요를 줄 수 없습니다.");
        if (likeRepository.existsByUserIdAndAndLikeReceiverId(likeGiverId, likeReceiverId))
            throw new InvalidLikeException("이미 좋아요를 준 대상입니다.");
    }

    @Transactional
    public void deleteLikeToUser(Long likeGiverId, Long likeReceiverId) {
        Likes likes = likeRepository.findByUserIdAndLikeReceiverId(likeGiverId, likeReceiverId)
                .orElseThrow(() -> {
                    throw new LikeNotFoundException("좋아요 받은 사람: " + likeReceiverId, likeGiverId);
                });
        User receiver = userRepository.findById(likeReceiverId).orElseThrow(() -> {
            throw new UserNotFoundException("좋아요를 받았던 유저를 찾을 수 없습니다.");
        });
        receiver.deleteLike();
        likeRepository.delete(likes);
    }

    /**
     * 유저가 좋아요 누른 프로젝트를 반환하는 매서드
     */
    public SliceTeamResponse getLikedSliceProjectTeamResponse(Long userId, Pageable pageable) {
        List<Likes> entireLikes = likeRepository.findLikesJoinProjectByUserId(userId);
        List<Long> projectIds = getTeamIdsByLike(entireLikes);
        Slice<Team> allByIdIn = teamRepository.findAllByIdIn(projectIds, pageable);
        return new SliceTeamResponse(teamSearchResponseList(allByIdIn.getContent()), allByIdIn.getSize(), allByIdIn.hasNext());
    }

    /**
     * 유저가 좋아요 누른 개인 프로젝트를 반환하는 매서드
     */
    public SliceTeamResponse getLikedSliceStudyTeamResponse(Long userId, Pageable pageable) {
        List<Likes> entireLikes = likeRepository.findLikesJoinStudyByUserId(userId);
        List<Long> studyIds = getTeamIdsByLike(entireLikes);
        Slice<Team> allByIdIn = teamRepository.findAllByIdIn(studyIds, pageable);
        return new SliceTeamResponse(teamSearchResponseList(allByIdIn.getContent()), allByIdIn.getSize(), allByIdIn.hasNext());
    }

    private List<Long> getTeamIdsByLike(List<Likes> entireLikes) {
        List<Long> teamIds = entireLikes.stream()
                .filter(likes -> likes.getTeam() != null)
                .map(likes -> likes.getTeam().getId()).collect(Collectors.toList());
        return teamIds;
    }


    private List<TeamSearchResponse> teamSearchResponseList(List<Team> teamList) {
        List<TeamSearchResponse> teamSearchResponseList = new ArrayList<>();
        Map<Long, User> userMap = getUserMap();
        teamList.stream()
                .forEach(team -> {
                    teamSearchResponseList.add(TeamSearchResponse.from(team, userMap));
                });
        return teamSearchResponseList;
    }

    private Map<Long, User> getUserMap() {
        Map<Long, User> userMap = new HashMap<>();
        userRepository.findAllUser().stream().forEach(user -> userMap.put(user.getId(), user));
        return userMap;
    }

    /**
     * 유저가 좋아요 누른 유저를 반환하는 메서드
     */
    public SliceUserCardResponse getLikedSliceUserCardResponse(Long userId, Pageable pageable) {
        List<Likes> entireLikes = likeRepository.findLikesJoinUserByUserId(userId);
        List<Long> userIds = entireLikes.stream().filter(likes -> likes.getLikeReceiver() != null)
                .map(likes -> likes.getLikeReceiver().getId()).collect(Collectors.toList());
        Slice<User> userSlice = userRepository.findAllByIdIn(userIds, pageable);
        return SliceUserCardResponse.builder()
                .userCardResponses(userCardResponseList(userSlice.getContent()))
                .size(userSlice.getSize()).hasNextSlice(userSlice.hasNext()).build();
    }

    private List<UserCardResponse> userCardResponseList(List<User> userList) {
        return userList.stream().map(user -> UserCardResponse.fromEntity(user,
                        userPositionRepository.findAllByUser(user).stream()
                                .max(Comparator.comparingInt(UserPosition::getTypeLevel).thenComparing(UserPosition::getId, Comparator.reverseOrder()))
                                .orElse(null)))
                        .collect(Collectors.toList());
    }

    /**
     * 유저가 좋아요를 눌렀는지 확인하는 메서드
     * @param userId
     * @param receiverId
     * @return
     */
    public UserLikeResponse checkUserLiked(Long userId, Long receiverId) {
        User likeReceiver = userRepository.findById(receiverId).orElseThrow(() -> {
            throw new UserNotFoundException("좋아요를 눌렀는지 체크 당하는 유저가 없습니다");
        });
        if (userId != null) { //로그인 한 사용자인 경우
            Optional<Likes> likes = likeRepository.findByUserIdAndLikeReceiver(userId, likeReceiver);
            return UserLikeResponse.builder()
                    .check(likes.isPresent())
                    .totalLike(likeReceiver.getLikes())
                    .build();
        } else { //로그인 안한 사용자인 경우
            return UserLikeResponse.builder()
                    .check(false)
                    .totalLike(likeReceiver.getLikes())
                    .build();
        }

    }
}
