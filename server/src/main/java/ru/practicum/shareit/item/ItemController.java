package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.convertToDto;
import static ru.practicum.shareit.utils.Constant.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_HEADER) long ownerId, @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping
    public List<GetItemDto> findAllItemsForOwner(@RequestHeader(USER_HEADER) long ownerId,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        return itemService.findAllItemsForOwner(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public GetItemDto findItemById(@RequestHeader(USER_HEADER) long ownerId, @PathVariable long id) {
        return itemService.findItemById(id, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(USER_HEADER) long ownerId, @PathVariable long id,
                              @RequestBody Map<String,String> formParams) {
        return convertToDto(itemService.updateItem(id, formParams, ownerId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USER_HEADER) long ownerId,
                                      @RequestParam String text,
                                      @RequestParam(required = false) Integer from,
                                      @RequestParam(required = false) Integer size) {
        return itemService.searchItems(text, from, size).stream()
                .map(ItemMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("{id}/comment")
    public GetCommentDto createComment(@RequestHeader(USER_HEADER) long userId, @PathVariable long id,
                                        @RequestBody Map<String, String> textMap) {
        return itemService.createComment(textMap.get("text"), LocalDateTime.now(), userId, id);
    }
}
