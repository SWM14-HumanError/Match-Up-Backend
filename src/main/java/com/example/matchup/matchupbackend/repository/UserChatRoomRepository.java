package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.UserChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    @Query("select userchatroom from UserChatRoom userchatroom " +
            "join fetch userchatroom.chatRoom " +
            "join fetch userchatroom.user " +
            "where userchatroom.user.id = :userId")
    Slice<UserChatRoom> findJoinChatRoomAndUserByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select userchatroom from UserChatRoom userchatroom " +
            "join fetch userchatroom.opponent " +
            "where userchatroom.chatRoom.id = :chatRoomId and userchatroom.user.id = :myId")
    Optional<UserChatRoom> findJoinOpponentByChatRoomId(@Param("chatRoomId") Long chatRoomId, @Param("myId") Long myId);
}
