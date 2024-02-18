package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Map;

public interface ItemStorage {
    Item createItem(Item item);

    List<Item> getAllItemsForOwner(long ownerId);

    Item getItemById(long id);

    Item updateItem(long id, Map<String, String> updatedParams);

    List<Item> searchItems(String text);
}
