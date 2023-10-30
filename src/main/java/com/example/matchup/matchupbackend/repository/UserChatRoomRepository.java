package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.UserChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    @Query("select userchatroom from UserChatRoom userchatroom " +
            "join fetch userchatroom.chatRoom " +
            "join fetch userchatroom.user " +
            "where userchatroom.user.id = :userId")
    Slice<UserChatRoom> findJoinChatRoomAndUserByUserId(@Param("userId") Long userId, Pageable pageable);
}
