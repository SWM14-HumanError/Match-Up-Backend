package com.example.matchup.matchupbackend.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Repository
public class ChatMessageRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public ChatMessageRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public ChatMessage save(ChatMessage chatMessage) {
        dynamoDBMapper.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> saveAll(List<ChatMessage> chatMessages) {
        dynamoDBMapper.batchSave(chatMessages);
        return chatMessages;
    }

    public ChatMessage findById(String id) {
        return dynamoDBMapper.load(ChatMessage.class, id);
    }

    public List<ChatMessage> findAll() {
        return dynamoDBMapper.scan(ChatMessage.class, new DynamoDBScanExpression());
    }

    public void deleteById(String id) {
        Optional<ChatMessage> chatMessage = ofNullable(dynamoDBMapper.load(ChatMessage.class, id));
        if (chatMessage.isPresent()) {
            dynamoDBMapper.delete(chatMessage);
        }
    }

    public List<ChatMessage> findByRoomId(Long roomId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":roomId", new AttributeValue().withN(roomId.toString()));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("roomId = :roomId")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(ChatMessage.class, scanExpression);
    }
}