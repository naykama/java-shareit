package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

public interface BookingService {
    GetBookingDto createBooking(BookingDto bookingDto, long bookerId);

    GetBookingDto responseBooking(long bookerId, long bookingId, boolean isApproved);
//
//    Booking findBookingById(long bookerId, long bookingId);
//
//    List<Booking> findBookingForCurrentUser(long bookerId, String state);
//
//    List<Booking> findBookingForOwner(long ownerId, String state);

}
