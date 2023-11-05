package com.example.matchup.matchupbackend.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.matchup.matchupbackend.config.DynamoDBConfig;
import com.example.matchup.matchupbackend.dto.request.chat.ChatMessageRequest;
import com.example.matchup.matchupbackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDBTable(tableName = "chatMessageDB")
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ChatMessage {
    private String chatId;
    private Long roomId;
    private String message;
    private Integer isRead;
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    private LocalDateTime sendTime;
    private String messageType;

    //sender-info
    private Long senderID;
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
    public Integer getIsRead() {
        return isRead;
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

    public void setChatId(String chat_id) {
        this.chatId = chat_id;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
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


    @Builder
    public ChatMessage(Long roomId, String message, String messageType, Long senderID, String senderName, String senderFace) {
        this.chatId = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.message = message;
        this.isRead = 0;
        this.sendTime = LocalDateTime.now();
        this.messageType = messageType;
        this.senderID = senderID;
    }

    public static ChatMessage fromDTO(ChatMessageRequest chatMessageRequest) {
        return ChatMessage.builder()
                .roomId(chatMessageRequest.getRoomId())
                .message(chatMessageRequest.getMessage())
                .messageType(chatMessageRequest.getType().getValue())
                .senderID(chatMessageRequest.getSender().getUserId())
                .build();
    }

    public static ChatMessage initChatRoom(User roomMaker, Long roomId) {
        return ChatMessage.builder()
                .roomId(roomId)
                .message(roomMaker.getNickname() + "님이 채팅방을 개설하였습니다.")
                .messageType("ENTER")
                .senderID(roomMaker.getId())
                .build();
    }

    public void readMessage() {
        this.isRead = 1;
    }
}
