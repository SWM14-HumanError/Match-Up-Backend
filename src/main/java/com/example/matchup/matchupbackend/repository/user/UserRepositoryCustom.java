package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface UserRepositoryCustom {
    Slice<User> findUserListByUserRequest(UserSearchRequest userSearchRequest, Pageable pageable);
    List<UserTag> findUserTagList();
}
