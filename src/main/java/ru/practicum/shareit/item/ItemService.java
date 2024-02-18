package ru.practicum.shareit.item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Item createItem(Item item);

    List<Item> getAllItemsForOwner(long ownerId);

    Item getItemById(long id);

    Item updateItem(long id, Map<String, String> updatedParams, long ownerId);

    List<Item> searchItems(String text);
}
