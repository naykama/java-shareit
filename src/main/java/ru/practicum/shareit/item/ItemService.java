package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long ownerId);

    List<GetItemDto> findAllItemsForOwner(long ownerId, Integer from, Integer size);

    GetItemDto findItemById(long id, long ownerId);

    Item updateItem(long id, Map<String, String> updatedParams, long ownerId);

    List<Item> searchItems(String text, Integer from, Integer size);

    GetCommentDto createComment(String text, LocalDateTime createDate, long userId, long itemId);
}
