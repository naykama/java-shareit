package ru.practicum.shareit.item.dto;
import ru.practicum.shareit.item.Item;

public class ItemMapper {
    public static ItemDto convertToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailableToRent());
    }

    public static Item convertToEntity(ItemDto itemDto, long ownerId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getIsAvailableToRent(),
                ownerId);
    }
}
