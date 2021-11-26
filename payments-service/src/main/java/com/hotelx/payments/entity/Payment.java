package com.hotelx.payments.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

/**
 * @author Fisnik Zejnullahu
 */
@DynamoDbBean
public class Payment {

    private String id;
    private double amount;
    private String transactionStatus;
    private String roomId;
    private Instant timestamp;

    public Payment() {
    }

    public Payment(String id, double amount, String transactionStatus, String roomId, Instant timestamp) {
        this.id = id;
        this.amount = amount;
        this.transactionStatus = transactionStatus;
        this.roomId = roomId;
        this.timestamp = timestamp;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
