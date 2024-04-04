package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import java.util.List;

import static ru.practicum.shareit.utils.Constant.USER_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public GetBookingDto createBooking(@RequestHeader(USER_HEADER) long bookerId, @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public GetBookingDto responseBooking(@RequestHeader(USER_HEADER) long ownerId,
                                         @PathVariable long bookingId,
                                         @RequestParam(name = "approved") boolean isApproved) {
        return bookingService.responseBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public GetBookingDto findBookingById(@RequestHeader(USER_HEADER) long userId,
                                         @PathVariable long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<GetBookingDto> findBookingForCurrentUser(@RequestHeader(USER_HEADER) long bookerId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(required = false) Integer from,
                                                         @RequestParam(required = false) Integer size) {
        return bookingService.findBookingForCurrentUser(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<GetBookingDto> findBookingForOwner(@RequestHeader(USER_HEADER) long ownerId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return bookingService.findBookingForOwner(ownerId, state, from, size);
    }
}
