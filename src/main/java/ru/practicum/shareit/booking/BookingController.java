package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
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
//
//    @GetMapping("/{bookingId}")
//    public GetBookingDto findBookingById(@RequestHeader(USER_HEADER) long bookerId,
//                                         @PathVariable long bookingId) {
//        return convertToGetDto(bookingService.findBookingById(bookerId, bookingId));
//    }
//
//    @GetMapping
//    public List<GetBookingDto> findBookingForCurrentUser(@RequestHeader(USER_HEADER) long bookerId,
//                                                         @RequestParam(defaultValue = "ALL") String state) {
//        return bookingService.findBookingForCurrentUser(bookerId, state).stream()
//                .map(BookingMapper::convertToGetDto)
//                .collect(Collectors.toList());
//    }
//
//    @GetMapping("/owner")
//    public List<GetBookingDto> findBookingForOwner(@RequestHeader(USER_HEADER) long ownerId,
//                                                   @RequestParam(defaultValue = "ALL") String state) {
//        return bookingService.findBookingForOwner(ownerId, state).stream()
//                .map(BookingMapper::convertToGetDto)
//                .collect(Collectors.toList());
//    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.isBefore(endDate) || startDate.isBefore(LocalDateTime.now())) {
            log.error("Booking dates are not correct: start: {}, end: {}", startDate, endDate);
            throw new IllegalArgumentException("Booking dates are not correct");
        }
    }

}
