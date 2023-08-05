package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.TeamSearchResponse;
import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserRepositoryCustom {
    Slice<UserCardResponse> findUserCardSliceByUserRequest(UserSearchRequest userSearchRequest, Pageable pageable);
}
