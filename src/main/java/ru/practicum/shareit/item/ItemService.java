package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ItemService {
    Item createItem(Item item);

    List<GetItemDto> findAllItemsForOwner(long ownerId);

    GetItemDto findItemById(long id, long ownerId);

    Item updateItem(long id, Map<String, String> updatedParams, long ownerId);

    List<Item> searchItems(String text);

    GetCommentDto createComment(String text, LocalDateTime createDate, long userId, long itemId);
}
