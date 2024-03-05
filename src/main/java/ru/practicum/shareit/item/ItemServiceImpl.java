package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRepository;
import ru.practicum.shareit.user.dto.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Item createItem(Item item) {
        userRepository.findById(item.getOwnerId()).orElseThrow(() -> new NotFoundException(
                String.format("User with id = %d not found", item.getOwnerId())));
        Item createdItem = itemRepository.save(item);
        log.info("Item with id = {} created", createdItem.getId());
        return createdItem;
    }

    @Override
    public List<Item> getAllItemsForOwner(long ownerId) {
        List<Item> items = itemRepository.getByOwnerId(ownerId);
        log.info("Items for owner with id = {} found, count of items = {}", ownerId, items.size());
        return items;
    }

    @Override
    public Item getItemById(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(
                                                                    String.format("Item with id = %d not found", id)));
        log.info("Item with id = {} found", id);
        return item;
    }

    @Override
    public Item updateItem(long id, Map<String, String> updatedParams, long ownerId) {
        userRepository.getById(ownerId);
        Item item = itemRepository.getItemById(id);
        if (ownerId != item.getOwnerId()) {
            log.error("User with id = {} cannot update item with id = {}. He is not owner", ownerId, id);
            throw new NotFoundException(
                    String.format("User with id = %d cannot update item with id = %d. He is not owner", ownerId, id)
            );
        }
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
        log.info("Item with id = {} updated", item.getId());
        return itemRepository.save(item);
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Items for search with text = {} found", text);
        return text.isEmpty() ? new ArrayList<>() : itemRepository
                .getByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(text, text);
    }
}
