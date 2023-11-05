package com.example.matchup.matchupbackend.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;

@EnableScan
public interface PagingChatMessageRepository extends PagingAndSortingRepository<ChatMessage, String> {
    Slice<ChatMessage> findByRoomId(Long roomId, Pageable pageable);
}
