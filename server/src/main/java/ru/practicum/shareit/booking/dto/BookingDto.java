package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class BookingDto {
    private final Long itemId;
    @JsonProperty("start")
    private final LocalDateTime startDate;
    @JsonProperty("end")
    private final LocalDateTime endDate;
}
