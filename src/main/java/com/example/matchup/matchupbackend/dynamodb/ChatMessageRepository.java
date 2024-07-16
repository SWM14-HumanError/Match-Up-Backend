package com.example.matchup.matchupbackend.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ChatMessage chatMessage = dynamoDBMapper.load(ChatMessage.class, id);
        if (chatMessage != null) {
            dynamoDBMapper.delete(chatMessage);
        }
    }

    public List<ChatMessage> findByRoomId(Long roomId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":roomId", new AttributeValue().withN(roomId.toString()));

        DynamoDBQueryExpression<ChatMessage> queryExpression = new DynamoDBQueryExpression<ChatMessage>()
                .withIndexName("RoomId-index") // 인덱스가 존재한다는 가정
                .withConsistentRead(false)
                .withKeyConditionExpression("roomId = :roomId")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.query(ChatMessage.class, queryExpression);
    }
}