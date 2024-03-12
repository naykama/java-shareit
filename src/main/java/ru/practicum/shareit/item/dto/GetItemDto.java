package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class GetItemDto extends ItemDto {
    private BookingDtoWithoutItem lastBooking;
    private BookingDtoWithoutItem nextBooking;
    private List<GetCommentDto> comments;

    public GetItemDto(long id, @NotEmpty(message = "name cannot be empty") String name,
                      @NotEmpty(message = "description cannot be empty") String description,
                      @NotNull Boolean isAvailableToRent) {
        super(id, name, description, isAvailableToRent);
    }
}
