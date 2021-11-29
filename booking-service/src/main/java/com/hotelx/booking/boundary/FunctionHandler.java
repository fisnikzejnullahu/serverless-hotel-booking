package com.hotelx.booking.boundary;

import com.hotelx.booking.RoomBookingException;
import com.hotelx.booking.control.BookingManager;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.io.StringReader;
import java.util.Map;

/**
 * @author Fisnik Zejnullahu
 */
public class FunctionHandler {

    private final BookingManager bookingManager;

    public FunctionHandler() {
        System.out.println("Initializing FunctionHandler...");
        String tableName = System.getenv("DDB_TABLE_NAME");
        if (tableName == null) {
            throw new RuntimeException("No DynamoDb table name specified!");
        }

        var reservationTimeMinutes = System.getenv("RESERVATION_TIME_MINUTES");

        if (reservationTimeMinutes == null) {
            reservationTimeMinutes = "1";
        }

        this.bookingManager = new BookingManager(tableName, Integer.parseInt(reservationTimeMinutes));
    }

    public String handle(Map<String, Object> event) throws RoomBookingException {
        JsonObject eventJson = Json.createObjectBuilder(event).build();
        var outputJsonBuilder = Json.createObjectBuilder();

        String message = null;
        String roomId = null;
        String type = null;

        if (event.containsKey("Payload")) {
            JsonObject payload = Json.createReader(new StringReader(eventJson.getString("Payload"))).readObject();
            if (payload.getString("type").equals("ProcessPaymentResult")) {
                type = "RoomReservationConfirmResult";
                roomId = payload.getString("roomId");
                message = bookingManager.confirmRoomReservation(roomId);
            }
        } else {
            if (eventJson.containsKey("StartReservation")) {
                roomId = eventJson.getString("roomId");
                message = bookingManager.reserveRoom(roomId);
                type = "ReserveRoomResult";
            } else if (eventJson.containsKey("CancelReservation")) {
                roomId = eventJson.getString("roomId");
                message = bookingManager.cancelRoomReservation(roomId);
                type = "CancelRoomReservationResult";
            }
        }

        return outputJsonBuilder
                .add("type", type)
                .add("message", message)
                .add("roomId", roomId)
                .build()
                .toString();
    }
}
