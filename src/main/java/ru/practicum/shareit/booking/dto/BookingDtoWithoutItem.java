package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class BookingDtoWithoutItem {
    @JsonProperty("id")
    private final Long bookingId;
    @JsonProperty("start")
    private final LocalDateTime startDate;
    @JsonProperty("end")
    private final LocalDateTime endDate;
    private final long bookerId;
}
