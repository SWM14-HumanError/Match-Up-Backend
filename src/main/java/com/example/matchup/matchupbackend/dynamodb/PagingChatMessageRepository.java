package com.example.matchup.matchupbackend.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.PagingAndSortingRepository;

@EnableScan
public interface PagingChatMessageRepository extends PagingAndSortingRepository<ChatMessage, String> {
}
