package ru.practicum.shareit.booking;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
    private boolean isRentApproved;
}
