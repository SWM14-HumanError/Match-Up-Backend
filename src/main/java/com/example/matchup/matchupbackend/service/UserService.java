package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.AdditionalUserInfoRequestDto;
import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserSearchRequest;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

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
                    return UserCardResponse.builder()
                            .userID(user.getId())
                            .profileImageURL(user.getPictureUrl())
                            .memberLevel(user.getUserLevel())
                            .nickname(user.getName())
                            .position(position)
                            .score(user.getReviewScore())
                            .like(user.getLikes())
                            .techStacks(user.returnStackList()).build();
                }
        ).collect(Collectors.toList());
    }

    public User findById(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("unexpected user"));
    }

    public Long saveAdditionalUserInfo(String token, AdditionalUserInfoRequestDto dto) {

        Long userId = tokenProvider.getUserId(token);
        User user = findById(userId);
        User updatedUser = user.updateFirstLogin(dto);
        userRepository.save(updatedUser);
        return userId;
    }

    public User findRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected refresh token."));
    }
}