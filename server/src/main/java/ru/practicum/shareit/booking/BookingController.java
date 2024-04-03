package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public GetBookingDto createBooking(@RequestHeader(USER_HEADER) long bookerId, @Valid @RequestBody BookingDto bookingDto) {
        validateDates(bookingDto.getStartDate(), bookingDto.getEndDate());
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
                                                         @RequestParam(required = false) @PositiveOrZero Integer from,
                                                         @RequestParam(required = false) @Positive Integer size) {
        return bookingService.findBookingForCurrentUser(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<GetBookingDto> findBookingForOwner(@RequestHeader(USER_HEADER) long ownerId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(required = false) @PositiveOrZero Integer from,
                                                   @RequestParam(required = false) @Positive Integer size) {
        return bookingService.findBookingForOwner(ownerId, state, from, size);
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.isBefore(endDate) || startDate.isBefore(LocalDateTime.now())) {
            log.error("Booking dates are not correct: start: {}, end: {}", startDate, endDate);
            throw new IllegalArgumentException("Booking dates are not correct");
        }
    }

}
