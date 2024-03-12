package ru.practicum.shareit.booking.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Slf4j
public class BookingMapper {

    public static GetBookingDto convertToGetDto(Booking booking) {
        return new GetBookingDto(booking.getId(), booking.getStartDate(),  booking.getEndDate(), booking.getStatus(),
                                                                            booking.getBooker(), booking.getItem());
    }

    public static Booking convertToEntity(BookingDto bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getStartDate(), bookingDto.getEndDate(), item, booker);
    }

    public static BookingDtoWithoutItem convertToDtoWithoutItem(Booking booking) {
        return new BookingDtoWithoutItem(booking.getId(), booking.getStartDate(), booking.getEndDate(),
                booking.getBooker().getId());
    }
}
