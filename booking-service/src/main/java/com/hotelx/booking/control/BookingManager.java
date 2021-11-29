package com.hotelx.booking.control;

import com.hotelx.booking.RoomBookingException;
import com.hotelx.booking.entity.Room;
import com.hotelx.booking.entity.RoomStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.Instant;

/**
 * @author Fisnik Zejnullahu
 */
public class BookingManager {

    private final String TABLE_NAME;
    private final DynamoDbEnhancedClient ENHANCED_DDB_CLIENT;
    private final int RESERVATION_TIME_MINUTES;
    private final String ROOM_RESERVE_PENDING_MESSAGE = "Please make your payment so you can reserve your room!";
    private final String ROOM_RESERVE_CONFIRM_MESSAGE = "Room was reserved successfully!";
    private final String ROOM_RESERVE_CANCEL_MESSAGE = "Room reservation was cancelled!";

    public BookingManager(String dynamoDbTableName, int defaultReservationTimeMinutes) {
        this.TABLE_NAME = dynamoDbTableName;
        this.RESERVATION_TIME_MINUTES = defaultReservationTimeMinutes;

        var ddb = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        this.ENHANCED_DDB_CLIENT = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();
    }

    public String reserveRoom(String roomId) throws RoomBookingException {
        Room room = getRoom(roomId);

        if (room != null) {
            if (room.getRoomStatus() == RoomStatus.RESERVED) {
                throw new RoomBookingException("Room is already reserved!");
            }

            if (room.getRoomStatus() == RoomStatus.RESERVE_PENDING && !reservationExpired(room.getReservationExpireIn())) {
                throw new RoomBookingException("Someone else is reserving this room!");
            }
        }

        var expireIn = Instant.now().plusSeconds(RESERVATION_TIME_MINUTES * 60).toEpochMilli();
        room = new Room(roomId, RoomStatus.RESERVE_PENDING, expireIn);
        putRoomItemInDd(room);
        return ROOM_RESERVE_PENDING_MESSAGE;
    }

    public String confirmRoomReservation(String roomId) {
        Room room = getRoom(roomId);
        room.setReservationExpireIn(0);
        room.setRoomStatus(RoomStatus.RESERVED);
        putRoomItemInDd(room);
        return ROOM_RESERVE_CONFIRM_MESSAGE;
    }

    public String cancelRoomReservation(String roomId) {
        Room room = getRoom(roomId);
        room.setReservationExpireIn(0);
        room.setRoomStatus(RoomStatus.AVAILABLE);
        putRoomItemInDd(room);
        return ROOM_RESERVE_CANCEL_MESSAGE;
    }

    public Room getRoom(String roomId) {
        try {
            //Create a DynamoDbTable object
            DynamoDbTable<Room> mappedTable = ENHANCED_DDB_CLIENT.table(TABLE_NAME, TableSchema.fromBean(Room.class));

            //Create a KEY object
            Key key = Key.builder()
                    .partitionValue(roomId)
                    .build();

            // Get the item by using the key
            return mappedTable.getItem(r -> r.key(key));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    // put room as item in dynamodb
    public void putRoomItemInDd(Room room) {
        try {
            DynamoDbTable<Room> roomDdbTable = ENHANCED_DDB_CLIENT.table(TABLE_NAME, TableSchema.fromBean(Room.class));

            // Put the customer data into a DynamoDB table
            roomDdbTable.putItem(room);
            System.out.println("Item put success!");
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private boolean reservationExpired(long reservationExpireIn) {
//        return reservationExpireIn > Instant.now().plusSeconds(DEFAULT_RESERVATION_TIME_MINUTES * 60).toEpochMilli();
        return Instant.now().toEpochMilli() > reservationExpireIn;
    }
}
