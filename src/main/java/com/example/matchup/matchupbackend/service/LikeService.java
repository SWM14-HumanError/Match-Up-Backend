package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.response.team.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamSearchResponse;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.entity.Likes;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.InvalidValueEx.InvalidLikeException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.LikeNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.repository.LikeRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final UserRepository userRepository;

    /**
     * 유저에게 좋아요를 주는 로직
     */
    @Transactional
    public void saveLikeToUser(Long likeGiverId, Long likeReceiverId) {
        if(likeGiverId.equals(likeReceiverId)) throw new InvalidLikeException();
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
     * 유저가 좋아요 누른 스터디를 반환하는 매서드
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

    /**
     * 유저가 좋아요 누른 유저를 반환하는 메서드
     */
    public SliceUserCardResponse getLikedSliceUserCardResponse(Long userId, Pageable pageable){
        List<Likes> entireLikes = likeRepository.findLikesJoinUserByUserId(userId);
        List<Long> userIds = entireLikes.stream().filter(likes -> likes.getUser() != null)
                .map(likes -> likes.getUser().getId()).collect(Collectors.toList());
    }



    public List<TeamSearchResponse> teamSearchResponseList(List<Team> teamList) {
        List<TeamSearchResponse> teamSearchResponseList = new ArrayList<>();
        Map<Long, User> userMap = getUserMap();
        teamList.stream()
                .forEach(team -> {
                    teamSearchResponseList.add(TeamSearchResponse.from(team, userMap));
                });
        return teamSearchResponseList;
    }

    public Map<Long, User> getUserMap() {
        Map<Long, User> userMap = new HashMap<>();
        userRepository.findAllUser().stream().forEach(user -> userMap.put(user.getId(), user));
        return userMap;
    }
}