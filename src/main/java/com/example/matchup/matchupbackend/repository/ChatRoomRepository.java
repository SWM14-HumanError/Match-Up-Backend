package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
