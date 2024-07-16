package com.example.matchup.matchupbackend.mongodb;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageV2Repository extends MongoRepository<ChatMessageV2, String> {
    Slice<ChatMessageV2> findByRoomIdOrderBySendTime(Long roomId, Pageable pageable);
    List<ChatMessageV2> findByRoomId(Long roomId);
}
