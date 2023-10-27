package com.example.matchup.matchupbackend.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {
}
