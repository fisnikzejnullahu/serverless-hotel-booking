package com.hotelx.booking.control;

import com.hotelx.booking.RoomBookingException;
import com.hotelx.booking.entity.Room;
import com.hotelx.booking.entity.RoomStatus;
import com.hotelx.booking.entity.RoomType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
* @author Fisnik Zejnullahu
*/
public class BookingManagerIT {

    private BookingManager bookingManager;
    private String roomId;

    @Before
    public void init() {
        this.bookingManager = new BookingManager("Rooms", 1);
        this.roomId = "100";
    }

    @Test
    public void testRoomReserve() throws RoomBookingException {
        this.bookingManager.reserveRoom(roomId);
        Room room = this.bookingManager.getRoom(roomId);
        assertEquals(RoomStatus.RESERVED, room.getRoomStatus());
    }

    @Test(expected = RoomBookingException.class)
    public void testRoomReserveException() throws Exception {
        this.bookingManager.reserveRoom(roomId);
        this.bookingManager.reserveRoom(roomId);
    }

    @Test
    public void testRoomReservationConfirm() throws RoomBookingException {
        this.bookingManager.reserveRoom(roomId);
        Room room = this.bookingManager.getRoom(roomId);
        assertEquals(RoomStatus.RESERVED, room.getRoomStatus());

        this.bookingManager.confirmRoomReservation(roomId);
        room = this.bookingManager.getRoom(roomId);
        assertEquals(RoomStatus.UNAVAILABLE, room.getRoomStatus());
    }

    @After
    public void testCancelRoomReservation() {
        this.bookingManager.cancelRoomReservation(roomId);
        Room room = this.bookingManager.getRoom(roomId);
        assertEquals(RoomStatus.AVAILABLE, room.getRoomStatus());
    }

}