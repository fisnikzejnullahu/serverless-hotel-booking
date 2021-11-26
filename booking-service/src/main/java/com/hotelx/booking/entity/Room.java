package com.hotelx.booking.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * @author Fisnik Zejnullahu
 */
@DynamoDbBean
public class Room {

    private String roomNumber;
    private RoomStatus roomStatus;
    private long reservationExpireIn;

    public Room() {
    }

    public Room(String roomNumber, RoomStatus roomStatus, long reservationExpireIn) {
        this.roomNumber = roomNumber;
        this.roomStatus = roomStatus;
        this.reservationExpireIn = reservationExpireIn;
    }

    @DynamoDbPartitionKey
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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
