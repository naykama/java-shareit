package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_HEADER) long ownerId, @Valid @RequestBody ItemDto itemDto) {
        Item item = convertToEntity(itemDto, ownerId);
        return convertToDto(itemService.createItem(item));
    }

    @GetMapping
    public List<ItemDto> getAllItemsForOwner(@RequestHeader(USER_HEADER) long ownerId) {
        return itemService.getAllItemsForOwner(ownerId).stream()
                .map(ItemMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id) {
        return convertToDto(itemService.getItemById(id));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(USER_HEADER) long ownerId, @PathVariable long id,
                              @RequestBody Map<String,String> formParams) {
        return convertToDto(itemService.updateItem(id, formParams, ownerId));
    }

    @GetMapping("/search")
    private List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text).stream()
                .map(ItemMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
