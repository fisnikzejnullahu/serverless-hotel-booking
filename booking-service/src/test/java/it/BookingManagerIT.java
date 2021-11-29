package it;

import com.hotelx.booking.RoomBookingException;
import com.hotelx.booking.control.BookingManager;
import com.hotelx.booking.entity.Room;
import com.hotelx.booking.entity.RoomStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * @author Fisnik Zejnullahu
 */
public class BookingManagerIT {

    private BookingManager bookingManager;
    private String roomId;

    @Before
    public void init() {
        this.bookingManager = new BookingManager("Rooms", 1);
        this.roomId = UUID.randomUUID().toString();
    }

    @Test
    public void testRoomReserve() throws RoomBookingException {
        this.bookingManager.reserveRoom(roomId);
        Room room = this.bookingManager.getRoom(roomId);
        assertEquals(RoomStatus.RESERVE_PENDING, room.getRoomStatus());
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
        assertEquals(RoomStatus.RESERVE_PENDING, room.getRoomStatus());

        this.bookingManager.confirmRoomReservation(roomId);
        room = this.bookingManager.getRoom(roomId);
        assertEquals(RoomStatus.RESERVED, room.getRoomStatus());
    }

    @After
    public void testCancelRoomReservation() {
        this.bookingManager.cancelRoomReservation(roomId);
        Room room = this.bookingManager.getRoom(roomId);
        assertEquals(RoomStatus.AVAILABLE, room.getRoomStatus());
    }

}