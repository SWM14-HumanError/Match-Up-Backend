package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.user.SliceUserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserSearchRequest;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public SliceUserCardResponse searchSliceUserCard(UserSearchRequest userSearchRequest, Pageable pageable)
    {
        Slice<UserCardResponse> userCardSliceByUserRequest = userRepository.findUserCardSliceByUserRequest(userSearchRequest, pageable);
        SliceUserCardResponse sliceUserCardResponse = SliceUserCardResponse.builder()
                .userCardResponses(userCardSliceByUserRequest.getContent())
                .size(userCardSliceByUserRequest.getSize())
                .hasNextSlice(userCardSliceByUserRequest.hasNext())
                .build();
        return sliceUserCardResponse;
    }


}
