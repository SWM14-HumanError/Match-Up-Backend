package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
}
