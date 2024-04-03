package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class BookingDto {
    @NotNull
    private final Long itemId;
    @JsonProperty("start")
    @NotNull
    private final LocalDateTime startDate;
    @JsonProperty("end")
    @NotNull
    private final LocalDateTime endDate;
}
