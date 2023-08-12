package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.dto.user.TechStack;
import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserSearchRequest;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserTag;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

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
        List<UserCardResponse> userCardResponseList = new ArrayList<>();
        return userList.stream().map(
                user -> {
                    Position position = new Position(user.getPosition(), user.getPositionLevel());
                    //Map<Long, List<TechStack>> userTags = userTagListToTeckStackMap(userRepository.findUserTagList());
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


}
/* public Map<Long,List<TechStack>> userTagListToTeckStackMap(List<UserTag> userTags)
    {
        Map<Long,List<TechStack>> teckStackMap = new HashMap<>();
        return userTags.stream().map(userTag -> {
            TechStack techStack = new TechStack(userTag.getId(), userTag.getTagName());
            teckStackMap.put(userTag.getUser().getId(), );
        })
    }

 */
