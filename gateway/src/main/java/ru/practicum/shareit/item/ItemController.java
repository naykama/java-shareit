package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

import static ru.practicum.shareit.utils.Constant.USER_HEADER;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_HEADER) long ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(ownerId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsForOwner(@RequestHeader(USER_HEADER) long ownerId,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.findAllItemsForOwner(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@RequestHeader(USER_HEADER) long ownerId, @PathVariable long id) {
        return itemClient.findItemById(id, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_HEADER) long ownerId, @PathVariable long id,
                              @RequestBody Map<String,String> formParams) {
        return itemClient.updateItem(id, formParams, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(USER_HEADER) long ownerId,
                                               @RequestParam(name = "text", defaultValue = "") String text,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.searchItems(ownerId, text, from, size);
    }

    @PostMapping("{id}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_HEADER) long userId, @PathVariable long id,
                                                @RequestBody @Valid CommentDto commentDto) {
        return itemClient.createComment(commentDto, userId, id);
    }
}
