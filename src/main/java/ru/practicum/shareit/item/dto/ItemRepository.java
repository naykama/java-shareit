package ru.practicum.shareit.item.dto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepository implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllItemsForOwner(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long id) {
        if (!items.containsKey(id)) {
            log.error("Item with id = {} not found", id);
            throw new NotFoundException(String.format("Item with id = %d not found", id));
        }
        return items.get(id);
    }

    @Override
    public Item updateItem(long id, Map<String, String> updatedParams) {
        Item item = items.get(id);
        for (String key : updatedParams.keySet()) {
            switch (key) {
                case "name":
                    item.setName(updatedParams.get(key));
                    break;
                case "description":
                    item.setDescription(updatedParams.get(key));
                    break;
                case "available":
                    item.setAvailableToRent(Boolean.parseBoolean(updatedParams.get(key)));
                    break;
            }
        }
        return item;
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> (StringUtils.containsIgnoreCase(item.getName(), text)
                        || StringUtils.containsIgnoreCase(item.getDescription(), text)) && item.isAvailableToRent())
                .collect(Collectors.toList());
    }
}
