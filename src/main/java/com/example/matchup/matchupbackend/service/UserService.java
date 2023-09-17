package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.UserCardResponse;
import com.example.matchup.matchupbackend.dto.request.user.AdditionalUserInfoRequest;
import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.dto.response.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import com.example.matchup.matchupbackend.repository.userposition.UserPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final UserPositionRepository userPositionRepository;

    public SliceUserCardResponse searchSliceUserCard(UserSearchRequest userSearchRequest, Pageable pageable) {
        Slice<User> userListByUserRequest = userRepository.findUserListByUserRequest(userSearchRequest, pageable);
        SliceUserCardResponse sliceUserCardResponse = SliceUserCardResponse.builder()
                .userCardResponses(userCardResponseList(userListByUserRequest.getContent()))
                .size(userListByUserRequest.getSize())
                .hasNextSlice(userListByUserRequest.hasNext())
                .build();
        return sliceUserCardResponse;
    }

    public List<UserCardResponse> userCardResponseList(List<User> userList) {
        return userList.stream().map(
                user -> {
                    Position position = new Position(user.getPosition(), user.getPositionLevel());
                    return UserCardResponse.of(user.getId(), user.getPictureUrl(), user.getUserLevel(),
                            user.getName(), position, user.getFeedbackScore(),
                            user.getLikes(), user.returnStackList());
                }
        ).collect(Collectors.toList());
    }

    @Transactional
    public Long saveAdditionalUserInfo(String authorizationHeader, AdditionalUserInfoRequest request) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "saveAdditionalUserInfo");

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("유저의 아이디를 찾을 수 없습니다."));;
        User updatedUser = user.updateFirstLogin(request);
        List<UserPosition> userPositions = user.getUserPositions().stream().map(position -> position.updateUserProfile(request.getUserPositionLevels())).toList();

        userRepository.save(updatedUser);
        userPositionRepository.saveAll(userPositions);

        return userId;
    }

    public User findRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected refresh token."));
    }
}