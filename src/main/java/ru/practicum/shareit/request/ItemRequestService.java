package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequest request);
}
