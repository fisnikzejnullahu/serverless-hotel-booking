package com.hotelx.booking.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * @author Fisnik Zejnullahu
 */
@DynamoDbBean
public class Room {

    private String roomId;
    private RoomStatus roomStatus;
    private long reservationExpireIn;

    public Room() {
    }

    public Room(String roomId, RoomStatus roomStatus, long reservationExpireIn) {
        this.roomId = roomId;
        this.roomStatus = roomStatus;
        this.reservationExpireIn = reservationExpireIn;
    }

    @DynamoDbPartitionKey
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public long getReservationExpireIn() {
        return reservationExpireIn;
    }

    public void setReservationExpireIn(long reservationExpireIn) {
        this.reservationExpireIn = reservationExpireIn;
    }
}
