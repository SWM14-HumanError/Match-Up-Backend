package com.example.matchup.matchupbackend.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.matchup.matchupbackend.config.DynamoDBConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDBTable(tableName = "chatMessageDB")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ChatMessage {

    private String chatId;
    private Long roomId;
    private String message;
    private Integer readerCount;
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    private LocalDateTime sendTime;
    private String messageType;

    //sender-info
    private Long senderID;
    private String senderName;
    private String senderFace;
    @DynamoDBHashKey(attributeName = "chat_id")
    public String getChatId() {
        return chatId;
    }

    @DynamoDBAttribute
    public Long getRoomId() {
        return roomId;
    }

    @DynamoDBAttribute
    public String getMessage() {
        return message;
    }

    @DynamoDBAttribute
    public Integer getReaderCount() {
        return readerCount;
    }

    @DynamoDBAttribute
    @DynamoDBTypeConverted( converter = DynamoDBConfig.LocalDateTimeConverter.class )
    public LocalDateTime getSendTime() {
        return sendTime;
    }

    @DynamoDBAttribute
    public String getMessageType() {
        return messageType;
    }

    @DynamoDBAttribute
    public Long getSenderID() {
        return senderID;
    }

    @DynamoDBAttribute
    public String getSenderName() {
        return senderName;
    }

    @DynamoDBAttribute
    public String getSenderFace() {
        return senderFace;
    }

    public void setChatId(String chat_id) {
        this.chatId = chat_id;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReaderCount(Integer readerCount) {
        this.readerCount = readerCount;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setSenderID(Long senderID) {
        this.senderID = senderID;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSenderFace(String senderFace) {
        this.senderFace = senderFace;
    }
    public ChatMessage(Long roomId, String message, Integer readerCount, LocalDateTime sendTime, String messageType, Long senderID, String senderName, String senderFace) {
        this.chatId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.message = message;
        this.readerCount = readerCount;
        this.sendTime = sendTime;
        this.messageType = messageType;
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderFace = senderFace;
    }

}
