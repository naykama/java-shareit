package ru.practicum.shareit.item.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Slf4j
public class ItemMapper {
    public static ItemDto convertToDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailableToRent());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static Item convertToEntity(ItemDto itemDto, long ownerId, ItemRequest request) {
        Item item = new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getIsAvailableToRent(),
                ownerId);
        if (request != null) {
            item.setRequest(request);
        }
        log.debug("convertToEntity: itemRequestId = {}", item.getRequest());
        return item;
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
