package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.Constant.USER_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(USER_HEADER) long userId,
										   @RequestBody @Valid BookItemRequestDto requestDto) {
		validateDates(requestDto.getStart(), requestDto.getEnd());
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> responseBooking(@RequestHeader(USER_HEADER) long ownerId,
										 @PathVariable long bookingId,
										 @RequestParam(name = "approved") boolean isApproved) {
		return bookingClient.responseBooking(ownerId, bookingId, isApproved);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_HEADER) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_HEADER) long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findBookingForOwner(@RequestHeader(USER_HEADER) long ownerId,
												@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
												@RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.findBookingForOwner(ownerId, state, from, size);
	}

	private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
		if (!startDate.isBefore(endDate) || startDate.isBefore(LocalDateTime.now())) {
			log.error("Booking dates are not correct: start: {}, end: {}", startDate, endDate);
			throw new IllegalArgumentException("Booking dates are not correct");
		}
	}
}
