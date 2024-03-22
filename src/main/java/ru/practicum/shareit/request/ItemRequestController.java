package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.PostItemRequestDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestMapping.convertToEntity;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_HEADER) long authorId, @Valid @RequestBody PostItemRequestDto description) {
        return service.createRequest(convertToEntity(description.getDescription(), authorId, LocalDateTime.now()));
    }

    @GetMapping
    public List<GetItemRequestDto> findAllRequests(@RequestHeader(USER_HEADER) long userId) {
        return service.findAllRequests(userId);
    }
}
