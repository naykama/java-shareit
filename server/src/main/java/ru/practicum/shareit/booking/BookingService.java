package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import java.util.List;

public interface BookingService {
    GetBookingDto createBooking(BookingDto bookingDto, long bookerId);

    GetBookingDto responseBooking(long ownerId, long bookingId, boolean isApproved);

    GetBookingDto findBookingById(long bookerOrOwnerId, long bookingId);

    List<GetBookingDto> findBookingForCurrentUser(long bookerId, String state, Integer from, Integer size);

    List<GetBookingDto> findBookingForOwner(long ownerId, String state, Integer from, Integer size);

}
