package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemStorage;
import ru.practicum.shareit.user.dto.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item createItem(Item item) {
        userStorage.getUserById(item.getOwnerId());
        return itemStorage.createItem(item);
    }

    @Override
    public List<Item> getAllItemsForOwner(long ownerId) {
        userStorage.getUserById(ownerId);
        return itemStorage.getAllItemsForOwner(ownerId);
    }

    @Override
    public Item getItemById(long id) {
        return itemStorage.getItemById(id);
    }

    @Override
    public Item updateItem(long id, Map<String, String> updatedParams, long ownerId) {
        userStorage.getUserById(ownerId);
        if (ownerId != itemStorage.getItemById(id).getOwnerId()) {
            log.error("User with id = {} cannot update item with id = {}. He is not owner", ownerId, id);
            throw new NotFoundException(
                    String.format("User with id = %d cannot update item with id = %d. He is not owner", ownerId, id)
            );
        }
        return itemStorage.updateItem(id, updatedParams);
    }

    @Override
    public List<Item> searchItems(String text) {
        return text.isEmpty() ? new ArrayList<>() : itemStorage.searchItems(text);
    }
}
