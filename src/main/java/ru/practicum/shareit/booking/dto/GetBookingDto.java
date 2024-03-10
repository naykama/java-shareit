package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class GetBookingDto {
    @JsonProperty("id")
    private final Long bookingId;
    @JsonProperty("start")
    private final LocalDateTime startDate;
    @JsonProperty("end")
    private final LocalDateTime endDate;
    private final Booking.StatusType status;
    private final User booker;
    private final Item item;
}
