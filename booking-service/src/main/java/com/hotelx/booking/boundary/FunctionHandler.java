package com.hotelx.booking.boundary;

import com.hotelx.booking.control.BookingManager;

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

    public String handle(Object event) {
        System.out.println("Handling: " + event.getClass().getName());
        return "Hi from booking service - " + System.currentTimeMillis();
    }
}
