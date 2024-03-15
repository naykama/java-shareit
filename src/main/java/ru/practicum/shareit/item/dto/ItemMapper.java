package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.item.Item;
import java.util.List;

public class ItemMapper {
    public static ItemDto convertToDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailableToRent());
        return itemDto;
    }

    public static Item convertToEntity(ItemDto itemDto, long ownerId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getIsAvailableToRent(),
                ownerId);
    }

    public static GetItemDto convertToGetDto(Item item, BookingDtoWithoutItem lastBooking, BookingDtoWithoutItem nextBooking,
                                             List<GetCommentDto> comments) {
        GetItemDto getItemDto = new GetItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailableToRent());
        getItemDto.setLastBooking(lastBooking);
        getItemDto.setNextBooking(nextBooking);
        getItemDto.setComments(comments);
        return getItemDto;
    }
}
