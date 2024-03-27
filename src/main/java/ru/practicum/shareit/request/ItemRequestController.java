package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestMapping.convertToEntity;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_HEADER) long authorId, @Valid @RequestBody PostItemRequestDto description) {
        return service.createRequest(convertToEntity(description.getDescription(), authorId, LocalDateTime.now()));
    }

    @GetMapping
    public List<GetItemRequestDto> findAll(@RequestHeader(USER_HEADER) long userId) {
        return service.findAllRequests(userId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> findAllFromOthersRequests(@RequestHeader(USER_HEADER) long userId,
                                                             @RequestParam(required = false) @PositiveOrZero Integer from,
                                                             @RequestParam(required = false) @Positive Integer size) {
        return from == null || size == null ? new ArrayList<>() : service.findAllFromOthersRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto findById(@RequestHeader(USER_HEADER) long userId, @PathVariable long requestId) {
        return service.findById(requestId, userId);
    }
}
