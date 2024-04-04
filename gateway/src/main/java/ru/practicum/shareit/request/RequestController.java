package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.Constant.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_HEADER) long authorId,
                                                @Valid @RequestBody RequestDto requestDto) {
        return requestClient.createRequest(authorId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_HEADER) long userId) {
        return requestClient.findAllRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllFromOthersRequests(@RequestHeader(USER_HEADER) long userId,
                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return requestClient.findAllFromOthersRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_HEADER) long userId, @PathVariable long requestId) {
        return requestClient.findById(requestId, userId);
    }
}
