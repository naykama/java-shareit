package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.*;
import static ru.practicum.shareit.item.dto.CommentMapper.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_HEADER) long ownerId, @Valid @RequestBody ItemDto itemDto) {
        Item item = convertToEntity(itemDto, ownerId);
        return convertToDto(itemService.createItem(item));
    }

    @GetMapping
    public List<GetItemDto> getAllItemsForOwner(@RequestHeader(USER_HEADER) long ownerId) {
        return itemService.getAllItemsForOwner(ownerId);
    }

    @GetMapping("/{id}")
    public GetItemDto getItemById(@RequestHeader(USER_HEADER) long ownerId, @PathVariable long id) {
        return itemService.getItemById(id, ownerId);
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

    @PostMapping("{id}/comment")
    private GetCommentDto createComment(@RequestHeader(USER_HEADER) long userId, @PathVariable long id,
                                        @RequestBody @NotNull Map<String, String> textMap) {
        log.debug("json прочитан");
        return itemService.createComment(textMap.get("text"), LocalDateTime.now(), userId, id);
//        return new GetCommentDto(userId,"fg", textMap.get("text"), LocalDateTime.now());
    }
}
