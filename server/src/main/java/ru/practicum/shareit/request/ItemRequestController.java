package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestMapping.convertToEntity;
import static ru.practicum.shareit.utils.Constant.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_HEADER) long authorId, @RequestBody ItemRequestDto description) {
        return service.createRequest(convertToEntity(description.getDescription(), authorId, LocalDateTime.now()));
    }

    @GetMapping
    public List<GetItemRequestDto> findAll(@RequestHeader(USER_HEADER) long userId) {
        return service.findAllRequests(userId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> findAllFromOthersRequests(@RequestHeader(USER_HEADER) long userId,
                                                             @RequestParam(required = false) Integer from,
                                                             @RequestParam(required = false) Integer size) {
        return from == null || size == null ? new ArrayList<>() : service.findAllFromOthersRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto findById(@RequestHeader(USER_HEADER) long userId, @PathVariable long requestId) {
        return service.findById(requestId, userId);
    }
}
